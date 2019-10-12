package com.wsk.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.wsk.bean.GoodsCarBean;
import com.wsk.bean.ShopInformationBean;
import com.wsk.bean.UserWantBean;
import com.wsk.pojo.AllKinds;
import com.wsk.pojo.BoughtShop;
import com.wsk.pojo.Classification;
import com.wsk.pojo.GoodsCar;
import com.wsk.pojo.GoodsOfOrderForm;
import com.wsk.pojo.OrderForm;
import com.wsk.pojo.ShopCar;
import com.wsk.pojo.ShopContext;
import com.wsk.pojo.ShopInformation;
import com.wsk.pojo.Specific;
import com.wsk.pojo.UserCollection;
import com.wsk.pojo.UserInformation;
import com.wsk.pojo.UserPassword;
import com.wsk.pojo.UserRelease;
import com.wsk.pojo.UserState;
import com.wsk.pojo.UserWant;
import com.wsk.response.BaseResponse;
import com.wsk.service.AllKindsService;
import com.wsk.service.BoughtShopService;
import com.wsk.service.ClassificationService;
import com.wsk.service.GoodsCarService;
import com.wsk.service.GoodsOfOrderFormService;
import com.wsk.service.OrderFormService;
import com.wsk.service.ShopCarService;
import com.wsk.service.ShopContextService;
import com.wsk.service.ShopInformationService;
import com.wsk.service.SpecificeService;
import com.wsk.service.UserCollectionService;
import com.wsk.service.UserInformationService;
import com.wsk.service.UserPasswordService;
import com.wsk.service.UserReleaseService;
import com.wsk.service.UserStateService;
import com.wsk.service.UserWantService;
import com.wsk.token.TokenProccessor;
import com.wsk.tool.SaveSession;
import com.wsk.tool.StringUtils;

import lombok.extern.slf4j.Slf4j;

/*import com.wsk.tool.OCR;
import com.wsk.tool.Pornographic;*/

/**
 * Created by wsk1103 on 2017/5/9.
 */
@Controller
@Slf4j
public class UserController {

    @Resource
    private UserInformationService userInformationService;
    @Resource
    private UserPasswordService userPasswordService;
    @Resource
    private UserCollectionService userCollectionService;
    @Resource
    private UserReleaseService userReleaseService;
    @Resource
    private BoughtShopService boughtShopService;
    @Resource
    private UserWantService userWantService;
    @Resource
    private ShopCarService shopCarService;
    @Resource
    private OrderFormService orderFormService;
    @Resource
    private GoodsOfOrderFormService goodsOfOrderFormService;
    @Resource
    private UserStateService userStateService;
    @Resource
    private ShopInformationService shopInformationService;
    @Resource
    private GoodsCarService goodsCarService;
    @Resource
    private SpecificeService specificeService;
    @Resource
    private ClassificationService classificationService;
    @Resource
    private AllKindsService allKindsService;
    @Resource
    private ShopContextService shopContextService;
    
    private static final Logger log = LoggerFactory.getLogger(UserController.class);


    //进入登录界面
    @RequestMapping(value = "/login.do", method = RequestMethod.GET)
    public String login(HttpServletRequest request, Model model) {
        String token = TokenProccessor.getInstance().makeToken();
        log.info("进入登录界面，token为:" + token);
        request.getSession().setAttribute("token", token);
        model.addAttribute("token", token);
        return "page/login_page";
    }

    //退出
    @RequestMapping(value = "/logout.do")
    public String logout(HttpServletRequest request) {
        try {
            request.getSession().removeAttribute("userInformation");
            request.getSession().removeAttribute("uid");
            System.out.println("logout");
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/home.do";
        }
        return "redirect:/";
    }

    //用户注册,拥有插入数据而已，没什么用的
    @RequestMapping(value = "/registered.do", method = RequestMethod.POST)
    public String registered(Model model,
                             @RequestParam String name, @RequestParam String phone, @RequestParam String password) {
        UserInformation userInformation = new UserInformation();
        userInformation.setUsername(name);
        userInformation.setPhone(phone);
        userInformation.setModified(new Date());
        userInformation.setCreatetime(new Date());
        if (userInformationService.insertSelective(userInformation) == 1) {
            int uid = userInformationService.selectIdByPhone(phone);
            UserPassword userPassword = new UserPassword();
            userPassword.setModified(new Date());
            password = StringUtils.getInstance().getMD5(password);
            userPassword.setPassword(password);
            userPassword.setUid(uid);
            int result = userPasswordService.insertSelective(userPassword);
            if (result != 1) {
                model.addAttribute("result", "fail");
                return "success";
            }
            model.addAttribute("result", "success");
            return "success";
        }
        model.addAttribute("result", "fail");
        return "success";
    }


    
  //验证登录
    @RequestMapping(value = "/checkPassword.do", method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String checkPassword(HttpServletRequest request,
                        @RequestParam String phone, @RequestParam String password, @RequestParam String token) {
        String loginToken = (String) request.getSession().getAttribute("token");
        if (StringUtils.getInstance().isNullOrEmpty(phone) || StringUtils.getInstance().isNullOrEmpty(password)) {
            return JSON.toJSONString(new BaseResponse(0, "手机和密码不能为空"));
        }
        //防止重复提交
        if (StringUtils.getInstance().isNullOrEmpty(token) || !token.equals(loginToken)) {
        	return JSON.toJSONString(new BaseResponse(0, "不能重复提交"));
        }
        boolean b = getId(phone, password, request);
        //失败，不存在该手机号码
        if (!b) {
        	return JSON.toJSONString(new BaseResponse(0, "手机号或密码错误"));
        }
        return JSON.toJSONString(new BaseResponse(1, "登录成功"));
    }
    

    //查看用户基本信息
    @RequestMapping(value = "/personal_info.do")
    public String personalInfo(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        }
        String personalInfoToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("personalInfoToken", personalInfoToken);
        model.addAttribute("token", personalInfoToken);
        model.addAttribute("userInformation", userInformation);
        return "page/personal/personal_info";
    }
    


    //完善用户基本信息，认证
    @RequestMapping(value = "/certification.do", method = RequestMethod.POST)
    @ResponseBody
    public Map certification(HttpServletRequest request,
                             @RequestParam(required = false) String userName,
                             @RequestParam(required = false) String realName,
                             @RequestParam(required = false) String clazz, @RequestParam String token,
                             @RequestParam(required = false) String sno, @RequestParam(required = false) String dormitory,
                             @RequestParam(required = false) String gender) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        Map<String, Integer> map = new HashMap<>();
        map.put("result", 0);
        //该用户还没有登录
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return map;
        }
        String certificationToken = (String) request.getSession().getAttribute("personalInfoToken");
        //防止重复提交
//        boolean b = token.equals(certificationToken);
        if (StringUtils.getInstance().isNullOrEmpty(certificationToken)) {
            return map;
        } else {
            request.getSession().removeAttribute("certificationToken");
        }
        if (userName != null && userName.length() < 25) {
            userName = StringUtils.getInstance().replaceBlank(userName);
            userInformation.setUsername(userName);
        } else if (userName != null && userName.length() >= 25) {
            return map;
        }
        if (realName != null && realName.length() < 25) {
            realName = StringUtils.getInstance().replaceBlank(realName);
            userInformation.setRealname(realName);
        } else if (realName != null && realName.length() >= 25) {
            return map;
        }
        if (clazz != null && clazz.length() < 25) {
            clazz = StringUtils.getInstance().replaceBlank(clazz);
            userInformation.setClazz(clazz);
        } else if (clazz != null && clazz.length() >= 25) {
            return map;
        }
        if (sno != null && sno.length() < 25) {
            sno = StringUtils.getInstance().replaceBlank(sno);
            userInformation.setSno(sno);
        } else if (sno != null && sno.length() >= 25) {
            return map;
        }
        if (dormitory != null && dormitory.length() < 25) {
            dormitory = StringUtils.getInstance().replaceBlank(dormitory);
            userInformation.setDormitory(dormitory);
        } else if (dormitory != null && dormitory.length() >= 25) {
            return map;
        }
        if (gender != null && gender.length() <= 2) {
            gender = StringUtils.getInstance().replaceBlank(gender);
            userInformation.setGender(gender);
        } else if (gender != null && gender.length() > 2) {
            return map;
        }
        int result = userInformationService.updateByPrimaryKeySelective(userInformation);
        if (result != 1) {
            //更新失败，认证失败
            return map;
        }
        //认证成功
        request.getSession().setAttribute("userInformation", userInformation);
        map.put("result", 1);
        return map;
    }
    
    /**
     * 更新用户时回显
     * @param id
     * @return
     */
    @RequestMapping(value="personal_info_update.do")
    public String updateUser(Integer id, Model model) {
    	UserInformation user = userInformationService.selectByPrimaryKey(id);
    	model.addAttribute("userInformation", user);
    	return "/page/personal/personal_info_update";
    }
    
    

    //enter the publishUserWant.do.html,进入求购页面
    @RequestMapping(value = "/require_product.do")
    public String enterPublishUserWant(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        }
        String error = request.getParameter("error");
        if (!StringUtils.getInstance().isNullOrEmpty(error)) {
            model.addAttribute("error", "error");
        }
        String publishUserWantToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("publishUserWantToken", publishUserWantToken);
        model.addAttribute("token", publishUserWantToken);
        model.addAttribute("userInformation", userInformation);
        return "page/require_product";
    }

    //修改求购商品
    @RequestMapping(value = "/modified_require_product.do")
    public String modifiedRequireProduct(HttpServletRequest request, Model model,
                                         @RequestParam int id) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        }
        String publishUserWantToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("publishUserWantToken", publishUserWantToken);
        model.addAttribute("token", publishUserWantToken);
        model.addAttribute("userInformation", userInformation);
        UserWant userWant = userWantService.selectByPrimaryKey(id);
        model.addAttribute("userWant", userWant);
        String sort = getSort(userWant.getSort());
        model.addAttribute("sort", sort);
        return "page/modified_require_product";
    }

    //publish userWant,发布求购
    @RequestMapping(value = "/publishUserWant.do")
//    @ResponseBody
    public String publishUserWant(HttpServletRequest request, Model model,
                                  @RequestParam String name,
                                  @RequestParam int sort, @RequestParam int quantity,
                                  @RequestParam double price, @RequestParam String remark,
                                  @RequestParam String token) {
//        Map<String, Integer> map = new HashMap<>();
        //determine whether the user exits
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            //if the user no exits in the session,
//            map.put("result", 2);
            return "redirect:/login.do";
        }
        String publishUserWantToke = (String) request.getSession().getAttribute("publishUserWantToken");
        if (StringUtils.getInstance().isNullOrEmpty(publishUserWantToke) || !publishUserWantToke.equals(token)) {
//            map.put("result", 2);
            return "redirect:require_product.do?error=3";
        } else {
            request.getSession().removeAttribute("publishUserWantToken");
        }
//        name = StringUtils.replaceBlank(name);
//        remark = StringUtils.replaceBlank(remark);
//        name = StringUtils.getInstance().txtReplace(name);
//        remark = StringUtils.getInstance().txtReplace(remark);
        try {
            if (name.length() < 1 || remark.length() < 1 || name.length() > 25 || remark.length() > 25) {
                return "redirect:require_product.do";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:require_product.do?error=1";
        }
        UserWant userWant = new UserWant();
        userWant.setModified(new Date());
        userWant.setName(name);
        userWant.setPrice(new BigDecimal(price));
        userWant.setQuantity(quantity);
        userWant.setRemark(remark);
        userWant.setUid((Integer) request.getSession().getAttribute("uid"));
        userWant.setSort(sort);
        int result;
        try {
            result = userWantService.insertSelective(userWant);
            if (result != 1) {
//                map.put("result", result);
                return "redirect:/require_product.do?error=2";
            }
        } catch (Exception e) {
            e.printStackTrace();
//            map.put("result", result);
            return "redirect:/require_product.do?error=2";
        }
//        map.put("result", result);
        return "redirect:/my_require_product.do";
    }

    //getUserWant,查看我的求购
    @RequestMapping(value = {"/my_require_product.do", "/my_require_product_page.do"})
    public String getUserWant(HttpServletRequest request, Model model) {
        List<UserWant> list;
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        }
        try {
            int uid = (int) request.getSession().getAttribute("uid");
//            list = selectUserWantByUid(4);
            list = selectUserWantByUid(uid);
            List<UserWantBean> userWantBeans = new ArrayList<>();
            for (UserWant userWant : list) {
                UserWantBean userWantBean = new UserWantBean();
                userWantBean.setId(userWant.getId());
                userWantBean.setModified(userWant.getModified());
                userWantBean.setName(userWant.getName());
                userWantBean.setPrice(userWant.getPrice().doubleValue());
                userWantBean.setUid(uid);
                userWantBean.setQuantity(userWant.getQuantity());
                userWantBean.setRemark(userWant.getRemark());
                userWantBean.setSort(getSort(userWant.getSort()));
                userWantBeans.add(userWantBean);
            }
            model.addAttribute("userWant", userWantBeans);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }
        model.addAttribute("userInformation", userInformation);
        return "page/personal/my_require_product_page";
    }
    

    //getUserWantCounts.do,查看求购总数
    @RequestMapping(value = "/getUserWantCounts.do")
    @ResponseBody
    public Map getUserWantCounts(HttpServletRequest request, Model model) {
        Map<String, Integer> map = new HashMap<>();
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            map.put("counts", -1);
            return map;
        }
        try {
            int counts = getUserWantCounts((Integer) request.getSession().getAttribute("uid"));
            map.put("counts", counts);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("counts", -1);
        }
        return map;
    }

    //删除求购
    @RequestMapping(value = "/deleteUserWant.do")
    public String deleteUserWant(HttpServletRequest request, @RequestParam int id) {
//        Map<String, Integer> map = new HashMap<>();
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            return "redirect:/login.do";
        }
        UserWant userWant = new UserWant();
        userWant.setId(id);
        userWant.setDisplay(0);
        try {
            int result = userWantService.updateByPrimaryKeySelective(userWant);
            if (result != 1) {
                return "redirect:my_require_product.do";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:my_require_product.do";
    }

    //收藏
    //add the userCollection
    @RequestMapping(value = "/addUserCollection.do")
    @ResponseBody
    public String addUserCollection(HttpServletRequest request, @RequestParam int sid) {
        //determine whether the user exits
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            //if the user no exits in the session,
            return JSON.toJSONString(new BaseResponse(0, ""));
        }
        UserCollection userCollection = new UserCollection();
        userCollection.setModified(new Date());
        userCollection.setSid(sid);
        userCollection.setUid((Integer) request.getSession().getAttribute("uid"));
        //begin insert the userCollection
        int result = userCollectionService.insertSelective(userCollection);
        if (result != 1) {
            return JSON.toJSONString(new BaseResponse(0, ""));
        }
        return JSON.toJSONString(new BaseResponse(1, ""));
    }
    
    /**
     * deleteUser
     * @param request
     * @param ucid
     * @return
     */
    @RequestMapping(value="deleteUser.do")
    public String deleteUser(HttpServletRequest request, Model model,Integer id) {
    	try {
    		UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
            if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
                return "redirect:/login.do";
            } else {
                model.addAttribute("userInformation", userInformation);
            }
    		userInformationService.deleteByPrimaryKey(id);
    		return "redirect:user_manager.do";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return "redirect:user_manager.do";
		}
    	
    }

    // delete the userCollection
    @RequestMapping(value = "/deleteUserCollection.do")
    @ResponseBody
    public String deleteUserCollection(HttpServletRequest request, @RequestParam int ucid) {
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            return JSON.toJSONString(new BaseResponse(0, ""));
        }
        UserCollection userCollection = new UserCollection();
//        userCollection.setUid((Integer) request.getSession().getAttribute("uid"));
//        userCollection.setSid(sid);
        userCollection.setId(ucid);
        userCollection.setModified(new Date());
        userCollection.setDisplay(0);
        int result;
        result = userCollectionService.updateByPrimaryKeySelective(userCollection);
        if (result != 1) {
            return JSON.toJSONString(new BaseResponse(0, ""));
        }
        return JSON.toJSONString(new BaseResponse(1, ""));
    }

    //购物车开始。。。。。。。。。。。
    //getShopCarCounts.do
    @RequestMapping(value = "/getShopCarCounts.do")
    @ResponseBody
    public String getShopCarCounts(HttpServletRequest request) {
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            return JSON.toJSONString(new BaseResponse(0, ""));
        }
        int uid = (int) request.getSession().getAttribute("uid");
        int counts = getShopCarCounts(uid);
        return JSON.toJSONString(new BaseResponse(1, ""));
    }

    //check the shopping cart,查看购物车
    @RequestMapping(value = "/shopping_cart.do")
    public String selectShopCar(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            userInformation = new UserInformation();
            model.addAttribute("userInformation", userInformation);
//            list.add(shopCar);
            return "redirect:/login.do";
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        int uid = userInformation.getId();
        List<GoodsCar> goodsCars = goodsCarService.selectByUid(uid);
        List<GoodsCarBean> goodsCarBeans = new ArrayList<>();
        for (GoodsCar goodsCar : goodsCars) {
            GoodsCarBean goodsCarBean = new GoodsCarBean();
            goodsCarBean.setUid(goodsCar.getUid());
            goodsCarBean.setSid(goodsCar.getSid());
            goodsCarBean.setModified(goodsCar.getModified());
            goodsCarBean.setId(goodsCar.getId());
            goodsCarBean.setQuantity(goodsCar.getQuantity());
            ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(goodsCar.getSid());
            goodsCarBean.setName(shopInformation.getName());
            goodsCarBean.setRemark(shopInformation.getRemark());
            goodsCarBean.setImage(shopInformation.getImage());
            goodsCarBean.setPrice(shopInformation.getPrice().doubleValue());
            goodsCarBean.setSort(getSort(shopInformation.getSort()));
            goodsCarBeans.add(goodsCarBean);
        }
        model.addAttribute("list", goodsCarBeans);
        return "page/shopping_cart";
    }

//    //通过购物车的id获取购物车里面的商品
//    @RequestMapping(value = "/selectGoodsOfShopCar")
//    @ResponseBody
//    public List<GoodsCar> selectGoodsCar(HttpServletRequest request) {
//        List<GoodsCar> list = new ArrayList<>();
//        GoodsCar goodsCar = new GoodsCar();
//        if (Empty.isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
//            list.add(goodsCar);
//            return list;
//        }
//        try {
//            int scid = shopCarService.selectByUid((Integer) request.getSession().getAttribute("uid")).getId();
//            list = goodsCarService.selectByUid(scid);
//            return list;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return list;
//        }
//    }

    //添加到购物车
    @RequestMapping(value = "/insertGoodsCar.do")
    @ResponseBody
    public String insertGoodsCar(HttpServletRequest request, @RequestParam int id) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return JSON.toJSONString(new BaseResponse(0, ""));
        }
        int uid = userInformation.getId();
        GoodsCar goodsCar = new GoodsCar();
        goodsCar.setDisplay(1);
        goodsCar.setModified(new Date());
        goodsCar.setQuantity(1);
        goodsCar.setUid(uid);
        goodsCar.setSid(id);
        int result = goodsCarService.insertSelective(goodsCar);
        return JSON.toJSONString(new BaseResponse(1, ""));
    }


    //删除购物车的商品
    @RequestMapping(value = "/deleteShopCar.do")
    @ResponseBody
    public String deleteShopCar(HttpServletRequest request, @RequestParam int id, @RequestParam int sid) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return JSON.toJSONString(new BaseResponse(0, ""));
        }
        int uid = userInformation.getId();
        GoodsCar goodsCar = new GoodsCar();
        goodsCar.setDisplay(0);
        goodsCar.setId(id);
        goodsCar.setSid(sid);
        goodsCar.setUid(uid);
        int result = goodsCarService.updateByPrimaryKeySelective(goodsCar);
        if (result != 1) {
            return JSON.toJSONString(new BaseResponse(0, ""));
        }
        return JSON.toJSONString(new BaseResponse(1, ""));
    }
    //发布商品
    @RequestMapping(value = "/insertGoods.do", method = RequestMethod.POST)
    public String insertGoods(@RequestParam String name, @RequestParam int level,
                              @RequestParam String remark, @RequestParam double price,
                              @RequestParam int sort, @RequestParam int quantity,
                              @RequestParam String token, @RequestParam(required = false) MultipartFile image,
                              @RequestParam int action, @RequestParam(required = false) int id,
                              HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        String goodsToken = (String) request.getSession().getAttribute("goodsToken");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out=response.getWriter();
        String fi = request.getSession().getServletContext().getRealPath("")+"/img/";
//        String publishProductToken = TokenProccessor.getInstance().makeToken();
//        request.getSession().setAttribute("token",publishProductToken);
        //防止重复提交
        if (StringUtils.getInstance().isNullOrEmpty(goodsToken) || !goodsToken.equals(token)) {
        	out.print("<script>alert('请勿重复提交')</script>");
           	return"redirect:publish_product.do?error=1";
        } else {
            request.getSession().removeAttribute("goodsToken");
        }
//        //从session中获得用户的基本信息
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        model.addAttribute("userInformation", userInformation);
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
        	out.print("<script>alert('用户信息错误')</script>");
            //如果用户不存在，
           return "redirect:/login.do";
        }
        name = StringUtils.getInstance().replaceBlank(name);
        remark = StringUtils.getInstance().replaceBlank(remark);
        //judge the data`s format
        if (StringUtils.getInstance().isNullOrEmpty(name) || StringUtils.getInstance().isNullOrEmpty(level) || StringUtils.getInstance().isNullOrEmpty(remark) || StringUtils.getInstance().isNullOrEmpty(price)
                || StringUtils.getInstance().isNullOrEmpty(sort) || StringUtils.getInstance().isNullOrEmpty(quantity) || name.length() > 25 || remark.length() > 122) {
            model.addAttribute("message", "请输入正确的格式!!!!!");
            model.addAttribute("token", goodsToken);
            request.getSession().setAttribute("goodsToken", goodsToken);
            out.print("<script>alert('请输入正确的格式!!!!!')</script>");
            return "page/publish_product";
        }
        //插入
        if (action == 1) {
            if (StringUtils.getInstance().isNullOrEmpty(image)) {
                model.addAttribute("message", "请选择图片!!!");
                model.addAttribute("token", goodsToken);
                request.getSession().setAttribute("goodsToken", goodsToken);
                out.print("<script>alert('请选择图片')</script>");
                return "redirect:publish_product.do";
            }
            String random = image.getOriginalFilename();
            int uid = (int) request.getSession().getAttribute("uid");
            String filePath = fi + uid;
            //创建图片文件夹
            if(!Files.isDirectory(Paths.get(filePath))){
                try {
                    Files.createDirectory(Paths.get(filePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String originalFileName = random;
//            System.out.println("原始文件名称：" + originalFileName);

            //获取文件类型，以最后一个`.`为标识
            String type = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
//            System.out.println("文件类型：" + type);
            //获取文件名称（不包含格式）
            String imgName = originalFileName.substring(0, originalFileName.lastIndexOf("."));

            //设置文件新名称: 当前时间+文件名称（不包含格式）
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String date = sdf.format(d);
            String fileName = date + imgName + "." + type;
//            System.out.println("新文件名称：" + fileName);

            try {
                Files.copy(image.getInputStream(), Paths.get(filePath, fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String imageName = "/img/"+uid+"/"+fileName;
            ShopInformation shopInformation = new ShopInformation();
            shopInformation.setName(name);
            shopInformation.setLevel(level);
            shopInformation.setRemark(remark);
            shopInformation.setPrice(new BigDecimal(price));
            shopInformation.setSort(sort);
            shopInformation.setQuantity(quantity);
            shopInformation.setModified(new Date());
            shopInformation.setImage(imageName);//This is the other uniquely identifies
            shopInformation.setThumbnails(imageName);
//        shopInformation.setUid(4);
            
            shopInformation.setUid(uid);
            try {
                int result = shopInformationService.insertSelective(shopInformation);
                //插入失败？？？
                if (result != 1) {
                    model.addAttribute("message", "请输入正确的格式!!!!!");
                    model.addAttribute("token", goodsToken);
                    request.getSession().setAttribute("goodsToken", goodsToken);
                    out.print("<script>alert('请输入正确的格式!!!!!')</script>");
                    return "page/publish_product";
                }
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("token", goodsToken);
                model.addAttribute("message", "请输入正确的格式!!!!!");
                request.getSession().setAttribute("goodsToken", goodsToken);
                out.print("<script>alert('请输入正确的格式!!!!!')</script>");
                return "page/publish_product";
            }
            int sid = shopInformationService.selectIdByImage(imageName);// get the id which is belongs shopInformation
            //将发布的商品的编号插入到用户的发布中
            UserRelease userRelease = new UserRelease();
            userRelease.setModified(new Date());
            userRelease.setSid(sid);
            userRelease.setUid(uid);
            try {
                int result = userReleaseService.insertSelective(userRelease);
                //如果关联失败，删除对应的商品和商品图片
                if (result != 1) {
                    //if insert failure,transaction rollback.
                    shopInformationService.deleteByPrimaryKey(sid);
//                shopPictureService.deleteByPrimaryKey(spid);
                    model.addAttribute("token", goodsToken);
                    model.addAttribute("message", "请输入正确的格式!!!!!");
                    request.getSession().setAttribute("goodsToken", goodsToken);
                    out.print("<script>alert('请输入正确的格式!!!!!')</script>");                
                    return "page/publish_product";
                }
            } catch (Exception e) {
                //if insert failure,transaction rollback.
                shopInformationService.deleteByPrimaryKey(sid);
                e.printStackTrace();
                model.addAttribute("token", goodsToken);
                model.addAttribute("message", "请输入正确的格式!!!!!");
                request.getSession().setAttribute("goodsToken", goodsToken);
                out.print("<script>alert('请输入正确的格式!!!!!')</script>");
                return "page/publish_product";
            }
            shopInformation.setId(sid);
            goodsToken = TokenProccessor.getInstance().makeToken();
            request.getSession().setAttribute("goodsToken", goodsToken);
            model.addAttribute("token", goodsToken);
            model.addAttribute("shopInformation", shopInformation);
            model.addAttribute("userInformation", userInformation);
            String sb = getSort(sort);
            model.addAttribute("sort", sb);
            model.addAttribute("action", 2);
            return "redirect:/my_publish_product_page.do";
        } else if (action == 2) {//更新商品
            ShopInformation shopInformation = new ShopInformation();
            shopInformation.setModified(new Date());
            shopInformation.setQuantity(quantity);
            shopInformation.setSort(sort);
            shopInformation.setPrice(new BigDecimal(price));
            shopInformation.setRemark(remark);
            shopInformation.setLevel(level);
            shopInformation.setName(name);
            shopInformation.setId(id);
            try {
                int result = shopInformationService.updateByPrimaryKeySelective(shopInformation);
                if (result != 1) {
                	out.print("<script>alert('保存失败')</script>");
                	return "redirect:publish_product.do";
                }
            } catch (Exception e) {
                e.printStackTrace();
                out.print("<script>alert('保存失败')</script>");
                return "redirect:publish_product.do";
            }
            goodsToken = TokenProccessor.getInstance().makeToken();
            request.getSession().setAttribute("goodsToken", goodsToken);
            model.addAttribute("token", goodsToken);
            shopInformation = shopInformationService.selectByPrimaryKey(id);
            model.addAttribute("userInformation", userInformation);
            model.addAttribute("shopInformation", shopInformation);
            model.addAttribute("action", 2);
            model.addAttribute("sort", getSort(sort));
        }
        out.print("<script>alert('保存成功')</script>");
        return "redirect:/my_publish_product_page.do";
    }

    //从发布的商品直接跳转到修改商品
    @RequestMapping(value = "/modifiedMyPublishProduct.do")
    public String modifiedMyPublishProduct(HttpServletRequest request, Model model,
                                           @RequestParam int id) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        }
        String goodsToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("goodsToken", goodsToken);
        model.addAttribute("token", goodsToken);
        ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(id);
        model.addAttribute("userInformation", userInformation);
        model.addAttribute("shopInformation", shopInformation);
        model.addAttribute("action", 2);
        model.addAttribute("sort", getSort(shopInformation.getSort()));
        return "page/publish_product";
    }

    //发表留言
    @RequestMapping(value = "/insertShopContext.do")
    @ResponseBody
    public Map insertShopContext(@RequestParam int id, @RequestParam String context, @RequestParam String token,
                                 HttpServletRequest request) {
        String goodsToken = (String) request.getSession().getAttribute("goodsToken");
        Map<String, String> map = new HashMap<>();
        map.put("result", "1");
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            map.put("result", "2");
            return map;
        }
        if (StringUtils.getInstance().isNullOrEmpty(goodsToken) || !token.equals(goodsToken)) {
            return map;
        }
        ShopContext shopContext = new ShopContext();
        shopContext.setContext(context);
        Date date = new Date();
        shopContext.setModified(date);
        shopContext.setSid(id);
        int uid = (int) request.getSession().getAttribute("uid");
        shopContext.setUid(uid);
        try {
            int result = shopContextService.insertSelective(shopContext);
            if (result != 1) {
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return map;
        }
        map.put("result", "1");
        map.put("username", userInformation.getUsername());
        map.put("context", context);
        map.put("time", StringUtils.getInstance().DateToString(date));
        return map;
    }

    //下架商品
    @RequestMapping(value = "/deleteShop.do")
    public String deleteShop(HttpServletRequest request, Model model, @RequestParam int id) {
//        Map<String, Integer> map = new HashMap<>();
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        ShopInformation shopInformation = new ShopInformation();
        shopInformation.setModified(new Date());
        shopInformation.setDisplay(0);
        shopInformation.setId(id);
        try {
            int result = shopInformationService.updateByPrimaryKeySelective(shopInformation);
            if (result != 1) {
                return "redirect:my_publish_product_page.do";
            }
            return "redirect:my_publish_product_page.do";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:my_publish_product_page.do";
        }
    }

    //查看发布的所有商品总数
    @RequestMapping(value = "/getReleaseShopCounts.do")
    @ResponseBody
    public Map getReleaseShopCounts(HttpServletRequest request) {
        Map<String, Integer> map = new HashMap<>();
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            map.put("counts", -1);
            return map;
        }
        int counts = getReleaseCounts((Integer) request.getSession().getAttribute("uid"));
        map.put("counts", counts);
        return map;
    }

    //查看我的发布的商品
    @RequestMapping(value = "/my_publish_product_page.do")
    public String getReleaseShop(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        int uid = (int) request.getSession().getAttribute("uid");
        List<ShopInformation> shopInformations = shopInformationService.selectUserReleaseByUid(uid);
        List<ShopInformationBean> list = new ArrayList<>();
        String stringBuffer;
//            int i=0;
        for (ShopInformation shopInformation : shopInformations) {
//                if (i>=5){
//                    break;
//                }
//                i++;
            stringBuffer = getSort(shopInformation.getSort());
            ShopInformationBean shopInformationBean = new ShopInformationBean();
            shopInformationBean.setId(shopInformation.getId());
            shopInformationBean.setName(shopInformation.getName());
            shopInformationBean.setLevel(shopInformation.getLevel());
            shopInformationBean.setPrice(shopInformation.getPrice().doubleValue());
            shopInformationBean.setRemark(shopInformation.getRemark());
            shopInformationBean.setSort(stringBuffer);
            shopInformationBean.setQuantity(shopInformation.getQuantity());
            shopInformationBean.setTransaction(shopInformation.getTransaction());
            shopInformationBean.setUid(shopInformation.getUid());
            shopInformationBean.setImage(shopInformation.getImage());
            list.add(shopInformationBean);
        }
        model.addAttribute("shopInformationBean", list);
        return "page/personal/my_publish_product_page";
    }
    
    @RequestMapping(value = "/user_manager.do")
    public String getUserList(HttpServletRequest request, Model model) {
    	 UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
         if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
             return "redirect:/login.do";
         } else {
             model.addAttribute("userInformation", userInformation);
         }
         
         List<UserInformation> userList = userInformationService.getAllUser();
         model.addAttribute("userList", userList);
         return "page/personal/user_list";
    }

    //更新商品信息


    private String getSort(int sort) {
        StringBuilder sb = new StringBuilder();
        Specific specific = selectSpecificBySort(sort);
        int cid = specific.getCid();
        Classification classification = selectClassificationByCid(cid);
        int aid = classification.getAid();
        AllKinds allKinds = selectAllKindsByAid(aid);
        sb.append(allKinds.getName());
        sb.append("-");
        sb.append(classification.getName());
        sb.append("-");
        sb.append(specific.getName());
        return sb.toString();
    }

    //查看用户收藏的货物的总数
    private int getCollectionCounts(int uid) {
        int counts;
        try {
            counts = userCollectionService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return counts;
    }

    //查看收藏，一次10个
    private List<UserCollection> selectContectionByUid(int uid, int start) {
        try {
            return userCollectionService.selectByUid(uid, (start - 1) * 10);
        } catch (Exception e) {
            e.printStackTrace();
            List<UserCollection> list = new ArrayList<>();
            list.add(new UserCollection());
            return list;
        }
    }

    //查看用户发布的货物的总数
    private int getReleaseCounts(int uid) {
        try {
            return userReleaseService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //查看发布的货物，一次10个
    private List<UserRelease> selectReleaseByUid(int uid, int start) {
        try {
            return userReleaseService.selectByUid(uid, (start - 1) * 10);
        } catch (Exception e) {
            e.printStackTrace();
            List<UserRelease> list = new ArrayList<>();
            list.add(new UserRelease());
            return list;
        }
    }

    //查看用户购买到的物品的总数
    private int getBoughtShopCounts(int uid) {
        try {
            return boughtShopService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //查看用户的购买，10个
    private List<BoughtShop> selectBoughtShopByUid(int uid, int start) {
        try {
            return boughtShopService.selectByUid(uid, (start - 1) * 10);
        } catch (Exception e) {
            e.printStackTrace();
            List<BoughtShop> list = new ArrayList<>();
            list.add(new BoughtShop());
            return list;
        }
    }

    //查看用户的求购总个数
    private int getUserWantCounts(int uid) {
        try {
            return userWantService.getCounts(uid);
        } catch (Exception e) {
            return -1;
        }
    }

    //求购列表10
    private List<UserWant> selectUserWantByUid(int uid) {
        try {
            return userWantService.selectMineByUid(uid);
        } catch (Exception e) {
            e.printStackTrace();
            List<UserWant> list = new ArrayList<>();
            list.add(new UserWant());
            return list;
        }
    }

    //我的购物车总数
    private int getShopCarCounts(int uid) {
        try {
            return shopCarService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //购物车列表  10
    private ShopCar selectShopCarByUid(int uid) {
        try {
            return shopCarService.selectByUid(uid);
        } catch (Exception e) {
            e.printStackTrace();
//            List<ShopCar> list
            return new ShopCar();
        }
    }

    //查看订单总数
    private int getOrderFormCounts(int uid) {
        try {
            return orderFormService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //订单列表 10个
    private List<OrderForm> selectOrderFormByUid(int uid, int start) {
        try {
            return orderFormService.selectByUid(uid, (start - 1) * 10);
        } catch (Exception e) {
            e.printStackTrace();
            List<OrderForm> list = new ArrayList<>();
            list.add(new OrderForm());
            return list;
        }
    }
    /**
     * 根据商品id查商品
     * @return
     */
    @RequestMapping(value = "selectShopByOfId.do")
    public String selectShopByOfId(HttpServletRequest request, Model model,Integer ofid) {
    	try {
    		UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
	        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
	            userInformation = new UserInformation();
	            model.addAttribute("userInformation", userInformation);
	        } else {
	            model.addAttribute("userInformation", userInformation);
	        }
	        List<GoodsOfOrderForm> goodList =  goodsOfOrderFormService.selectByOFid(ofid);
	        model.addAttribute("goodList",goodList);
    	} catch (Exception e) {
    		log.error(e.getMessage(), e);
    	
		}
    	return "/page/goodoforder";
    }
    /**
     * 查询订单
     * @param ofid
     * @return
     */
    @RequestMapping(value="/orderForm.do")
    private String selectOrderForm(HttpServletRequest request, Model model){
    	try{
	    	UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
	        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
	            userInformation = new UserInformation();
	            model.addAttribute("userInformation", userInformation);
	        } else {
	            model.addAttribute("userInformation", userInformation);
	        }
	        
		    	List<OrderForm> orders = orderFormService.selectOrderForm(userInformation.getId());
		    	
		     	model.addAttribute("listOrder", orders);
	    	}catch(Exception e) {
	    		log.error(e.getMessage(), e);
	    	}
	    	return "page/orderForm";
		}

    //订单中的商品
    private List<GoodsOfOrderForm> selectGoodsOfOrderFormByOFid(int ofid) {
        try {
            return goodsOfOrderFormService.selectByOFid(ofid);
        } catch (Exception e) {
            e.printStackTrace();
            List<GoodsOfOrderForm> list = new ArrayList<>();
            list.add(new GoodsOfOrderForm());
            return list;
        }
    }
    /**
     * 删除订单
     * @param uid
     * @return
     */
    @RequestMapping(value = "deleteOrder.do")
    public String deleteOrder(HttpServletRequest request, Model model, Integer id) {
    	try {
    		UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
	        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
	            userInformation = new UserInformation();
	            model.addAttribute("userInformation", userInformation);
	        } else {
	            model.addAttribute("userInformation", userInformation);
	        }
    		int o = orderFormService.deleteByPrimaryKey(id);
    		return "redirect:orderForm.do";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return "/index";
		}
		
    	
    }
    //查看用户的状态
    private UserState selectUserStateByUid(int uid) {
        try {
            return userStateService.selectByUid(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return new UserState();
        }
    }

    //判断该手机号码及其密码是否一一对应
    private boolean getId(String phone, String password, HttpServletRequest request) {
        int uid = userInformationService.selectIdByPhone(phone);
        if (uid == 0 || StringUtils.getInstance().isNullOrEmpty(uid)) {
            return false;
        }
        UserInformation userInformation = userInformationService.selectByPrimaryKey(uid);
        if (null == userInformation) {
            return false;
        }
        password = StringUtils.getInstance().getMD5(password);
        String password2 = userPasswordService.selectByUid(userInformation.getId()).getPassword();
        if (!password.equals(password2)) {
            return false;
        }
        //如果密码账号对应正确，将userInformation存储到session中
        request.getSession().setAttribute("userInformation", userInformation);
        request.getSession().setAttribute("uid", uid);
        SaveSession.getInstance().save(phone, System.currentTimeMillis());
        return true;
    }

    //获取最详细的分类，第三层
    private Specific selectSpecificBySort(int sort) {
        return specificeService.selectByPrimaryKey(sort);
    }

    //获得第二层分类
    private Classification selectClassificationByCid(int cid) {
        return classificationService.selectByPrimaryKey(cid);
    }

    //获得第一层分类
    private AllKinds selectAllKindsByAid(int aid) {
        return allKindsService.selectByPrimaryKey(aid);
    }

    public void save(ShopInformation shopInformation, UserRelease userRelease) {
        shopInformationService.insertSelective(shopInformation);
        userReleaseService.insertSelective(userRelease);
    }

    //循环插入商品
    //发布商品
    @RequestMapping(value = "/test")
    public String insertGoods() {

        //begin insert the shopInformation to the MySQL
//            ShopInformation shopInformation = new ShopInformation();
//            shopInformation.setName(name);
//            shopInformation.setLevel(level);
//            shopInformation.setRemark(remark);
//            shopInformation.setPrice(new BigDecimal(price));
//            shopInformation.setSort(sort);
//            shopInformation.setQuantity(quantity);
//            shopInformation.setModified(new Date());
//            shopInformation.setImage(image);//This is the other uniquely identifies
//            shopInformation.setUid(uid);
//            //将发布的商品的编号插入到用户的发布中
//            UserRelease userRelease = new UserRelease();
//            userRelease.setModified(new Date());
//            userRelease.setSid(sid);
//            userRelease.setUid(uid);
//            shopInformation.setId(sid);
        Random random = new Random();
        ShopInformation shopInformation;
        UserRelease userRelease;
        int level, uid, quantity;
        double price;
        for (int i = 1, k = 1, j = 189; i < 1000; i++, j++, k++) {
            if (k > 94) {
                k = 1;
            }
            level = random.nextInt(10) + 1;
            price = Math.random() * 1000 + 1;
            quantity = random.nextInt(10) + 1;
            uid = random.nextInt(100) + 1;
            shopInformation = new ShopInformation();
            shopInformation.setId(j);
            shopInformation.setName("百年孤独");
            shopInformation.setModified(new Date());
            shopInformation.setLevel(level);
            shopInformation.setRemark("看上的请联系我，QQ：test，微信：test");
//            double price = Math.random()*1000.00+1;
            shopInformation.setPrice(new BigDecimal(price));
            shopInformation.setSort(k);
            shopInformation.setQuantity(quantity);
            shopInformation.setImage("/image/QyBHYiMfYQ4XZFCqxEv0.jpg");
//            int uid = random.nextInt(100)+1;
            shopInformation.setUid(uid);
//            userRelease = new UserRelease();
//            userRelease.setUid(uid);
//            userRelease.setSid(j);
//            userRelease.setModified(new Date());
//            userRelease.setDisplay(1);
            shopInformationService.updateByPrimaryKeySelective(shopInformation);
//            userReleaseService.insertSelective(userRelease);
        }
        System.out.println("success");
        return "page/publish_product";
    }
    
}
