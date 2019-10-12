package com.wsk.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wsk.bean.ShopContextBean;
import com.wsk.bean.ShopInformationBean;
import com.wsk.bean.UserWantBean;
import com.wsk.pojo.*;
import com.wsk.response.BaseResponse;
import com.wsk.service.*;
import com.wsk.token.TokenProccessor;
import com.wsk.tool.OrderNumberUtil;
import com.wsk.tool.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wsk1103 on 2017/5/14.
 */
@Controller
public class GoodsController {
    @Resource
    private ShopInformationService shopInformationService;
    @Resource
    private ShopContextService shopContextService;
    @Resource
    private UserInformationService userInformationService;
    @Resource
    private SpecificeService specificeService;
    @Resource
    private ClassificationService classificationService;
    @Resource
    private AllKindsService allKindsService;
    @Resource
    private UserWantService userWantService;
    
    @Resource
    private OrderFormService orderFormService;
    
    @Resource
    GoodsOfOrderFormService goodsOfOrderFormService;
    @Resource
    GoodsCarService goodsCarService;
    
    private static final Logger log = LoggerFactory.getLogger(GoodsController.class);

    //进入到发布商品页面
    @RequestMapping(value = "/publish_product.do", method = RequestMethod.GET)
    public String publish(HttpServletRequest request, Model model) {
        //先判断用户有没有登录
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            //如果没有登录
            return "redirect:/login.do";
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        //如果登录了，判断该用户有没有经过认证
        try {
            String realName = userInformation.getRealname();
            String sno = userInformation.getSno();
            String dormitory = userInformation.getDormitory();
            if (StringUtils.getInstance().isNullOrEmpty(realName) || StringUtils.getInstance().isNullOrEmpty(sno) || StringUtils.getInstance().isNullOrEmpty(dormitory)) {
                //没有
                model.addAttribute("message", "请先认证真实信息");
                return "redirect:personal_info.do";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login.do";
        }
        String goodsToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("goodsToken", goodsToken);
        model.addAttribute("shopInformation", new ShopInformation());
        model.addAttribute("action", 1);
        model.addAttribute("token", goodsToken);
        return "page/publish_product";
    }

    //模糊查询商品
    @RequestMapping(value = "/findShopByName.do")
    public String findByName(HttpServletRequest request, Model model,
                             @RequestParam String name) {
        try {
            List<ShopInformation> shopInformations = shopInformationService.selectByName(name);
            UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
            if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
                userInformation = new UserInformation();
                model.addAttribute("userInformation", userInformation);
            } else {
                model.addAttribute("userInformation", userInformation);
            }
            List<ShopInformationBean> shopInformationBeans = new ArrayList<>();
            String sortName;
            for (ShopInformation shopInformation : shopInformations) {
                int sort = shopInformation.getSort();
                sortName = getSort(sort);
                ShopInformationBean shopInformationBean = new ShopInformationBean();
                shopInformationBean.setId(shopInformation.getId());
                shopInformationBean.setName(shopInformation.getName());
                shopInformationBean.setLevel(shopInformation.getLevel());
                shopInformationBean.setRemark(shopInformation.getRemark());
                shopInformationBean.setPrice(shopInformation.getPrice().doubleValue());
                shopInformationBean.setQuantity(shopInformation.getQuantity());
                shopInformationBean.setTransaction(shopInformation.getTransaction());
                shopInformationBean.setSort(sortName);
                shopInformationBean.setUid(shopInformation.getUid());
                shopInformationBean.setImage(shopInformation.getImage());
                shopInformationBeans.add(shopInformationBean);
            }
            model.addAttribute("shopInformationBean", shopInformationBeans);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:mall_page.do";
        }
        return "page/mall_page";
    }

    //进入查看商品详情
    @RequestMapping(value = "/selectById.do")
    public String selectById(@RequestParam int id,
                             HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            userInformation = new UserInformation();
            model.addAttribute("userInformation", userInformation);
        }
        try {
            ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(id);
            model.addAttribute("shopInformation", shopInformation);
            List<ShopContext> shopContexts = shopContextService.selectById(id);
            List<ShopContextBean> shopContextBeans = new ArrayList<>();
            for (ShopContext s : shopContexts) {
                ShopContextBean shopContextBean = new ShopContextBean();
                UserInformation u = userInformationService.selectByPrimaryKey(s.getUid());
                shopContextBean.setContext(s.getContext());
                shopContextBean.setId(s.getId());
                shopContextBean.setModified(s.getModified());
                shopContextBean.setUid(u.getId());
                shopContextBean.setUsername(u.getUsername());
                shopContextBeans.add(shopContextBean);
            }
            String sort = getSort(shopInformation.getSort());
            String goodsToken = TokenProccessor.getInstance().makeToken();
            request.getSession().setAttribute("goodsToken", goodsToken);
            model.addAttribute("token", goodsToken);
            model.addAttribute("sort", sort);
            model.addAttribute("userInformation", userInformation);
            model.addAttribute("shopContextBeans", shopContextBeans);
            return "page/product_info";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }
    }

    //进入到求购商城
    @RequestMapping(value = "/require_mall.do")
    public String requireMall(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            userInformation = new UserInformation();
            model.addAttribute("userInformation", userInformation);
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        List<UserWant> userWants = userWantService.selectAll();
        List<UserWantBean> list = new ArrayList<>();
        for (UserWant userWant : userWants) {
            UserWantBean u = new UserWantBean();
            u.setSort(getSort(userWant.getSort()));
            u.setRemark(userWant.getRemark());
            u.setQuantity(userWant.getQuantity());
            u.setPrice(userWant.getPrice().doubleValue());
            u.setUid(userWant.getUid());
            u.setId(userWant.getId());
            u.setModified(userWant.getModified());
            u.setName(userWant.getName());
            list.add(u);
        }
        model.addAttribute("list", list);
        return "page/require_mall";
    }

    //通过id查看商品的详情
    @RequestMapping(value = "/findShopById.do")
    @ResponseBody
    public ShopInformation findShopById(@RequestParam int id) {
        return shopInformationService.selectByPrimaryKey(id);
    }

    //通过分类选择商品
    @RequestMapping(value = "/selectBySort.do")
    @ResponseBody
    public List<ShopInformation> selectBySort(@RequestParam int sort) {
        return shopInformationService.selectBySort(sort);
    }

    //分页查询
    @RequestMapping(value = "/selectByCounts.do")
    @ResponseBody
    public List<ShopInformation> selectByCounts(@RequestParam int counts) {
        Map<String, Integer> map = new HashMap<>();
        map.put("start", (counts - 1) * 12);
        map.put("end", 12);
        return shopInformationService.selectTen(map);
    }
    
    /**
     * 商品审核
     */
    @RequestMapping(value="/product_examine.do")
    public String goodsListForExamine(HttpServletRequest request, Model model) {
    	UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            userInformation = new UserInformation();
            model.addAttribute("userInformation", userInformation);
        } else {
            model.addAttribute("userInformation", userInformation);
        }
    	List<ShopInformation> goods = shopInformationService.getAllGoods();
    	model.addAttribute("goodsList", goods);
    	System.out.println("goodsList"+model);
    	return "page/shopping_examine_list";
    }
    
    /**
     * 审核
     * @param id
     * @return
     */
    
    @RequestMapping(value="/save_product_examine.do")
    @ResponseBody
    public String updateGoodStatus(HttpServletRequest request, Integer id, Model model) {
    	UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            userInformation = new UserInformation();
            model.addAttribute("userInformation", userInformation);
        } else {
            model.addAttribute("userInformation", userInformation);
        }
    	int result = shopInformationService.updateByStatus(id);
    	if (result > 0) {
    		return JSON.toJSONString(new BaseResponse(1, "操作成功"));
    	} else {
    		return JSON.toJSONString(new BaseResponse(0, "操作失败，请稍后重试"));
    		
    	}
    }
    
//    //通过id查看商品详情
//    @RequestMapping(value = "/showShop")
//    public String showShop(@RequestParam int id, HttpServletRequest request, Model model) {
//        ShopInformation shopInformation =
//    }

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

    private String getSort(int sort) {
        StringBuilder sb = new StringBuilder();
        Specific specific = selectSpecificBySort(sort);
        int cid = specific.getCid();
        Classification classification = selectClassificationByCid(cid);
        int aid = classification.getAid();
        AllKinds allKinds = selectAllKindsByAid(aid);
        String allName = allKinds.getName();
        sb.append(allName);
        sb.append("-");
        sb.append(classification.getName());
        sb.append("-");
        sb.append(specific.getName());
        return sb.toString();
    }
    
    @RequestMapping(value="sale_order.do", method=RequestMethod.POST)
    @ResponseBody
    public String saveOrder(HttpServletRequest request, Model model, String datajsonStr) {
    	try {
    		UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
            if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
                userInformation = new UserInformation();
                model.addAttribute("userInformation", userInformation);
            } else {
                model.addAttribute("userInformation", userInformation);
            }
        	JSONArray jsonArray = JSONArray.parseArray(datajsonStr);
        	String orderContext = OrderNumberUtil.generateUniqueKey();
        	OrderForm order = new OrderForm();
        	order.setContext(orderContext);
        	order.setDisplay(1);
        	order.setModified(new Date());
        	order.setUid(userInformation.getId());
        	order.setPrice(jsonArray.getJSONObject(jsonArray.size()-1).getBigDecimal("allSum"));
        	int ofid = orderFormService.insert(order);
        	for (int i = 0; i < jsonArray.size(); i++) {
        		JSONObject good = jsonArray.getJSONObject(i);
        		GoodsOfOrderForm detal = new GoodsOfOrderForm();
        		detal.setDisplay(1);
        		detal.setModified(new Date());
        		detal.setSid(good.getIntValue("id"));
        		detal.setOfid(ofid);
        		detal.setQuantity(good.getInteger("sum"));
        		goodsOfOrderFormService.insert(detal);
        		goodsCarService.deleteShopCarBySid(good.getIntValue("id"));
        	} 
		} catch (Exception e) {
			log.error(e.getMessage(), e);
 			return JSON.toJSONString(new BaseResponse(0, "系统繁忙，请稍后重试"));
 		}
		return JSON.toJSONString(new BaseResponse(1, "操作成功"));
    }

}
