/**
 * Created by alone on 2017/5/15.
 */
var meidi = createObject(1, '美的');
var geli = createObject(2, '格力');
var haier = createObject(3, '海尔');
var aux = createObject(4, '奥克斯');
var songxia = createObject(5, '松下');
var kangjia = createObject(6, '康佳');
var xiaomi = createObject(7, '小米');
var zhigao = createObject(8, '志高');
var qitaA = createObject(9, '其他');
var huawei = createObject(10, '华为');
var pingguo = createObject(11, '苹果');
var sanxing = createObject(12, '三星');
var dianfengshan = new Object();
dianfengshan.name = '电风扇';
dianfengshan.content = [meidi, geli, xiaomi, aux, songxia];

var kongtiao = new Object();
kongtiao.name='空调';
kongtiao.content=[meidi, geli, zhigao, haier, aux, songxia, kangjia];

var kongqijinghuaqi = new Object();
kongqijinghuaqi.name='空气净化器';
kongqijinghuaqi.content=[meidi, geli, zhigao, haier, aux, songxia];

var dianbingxiang = new Object();
dianbingxiang.name='电冰箱';
dianbingxiang.content=[meidi, geli, haier, songxia];

var zhilengji = new Object();
zhilengji.name='制冷机';
zhilengji.content=[meidi, aux, haier, geli];


var xiyiji = new Object();
xiyiji.name='洗衣机';
xiyiji.content=[songxia, zhigao, haier, geli];

var xichenqi = new Object();
xichenqi.name='吸尘器';
xichenqi.content=[meidi, geli, zhigao, haier, xiaomi, songxia];

var diannuanqi = new Object();
diannuanqi.name='电暖气';
diannuanqi.content=[meidi, geli, haier, songxia];

var dianretan = new Object();
dianretan.name='电热毯';
dianretan.content=[meidi, aux, haier, geli];


var diannao = new Object();
diannao.name='电脑';
diannao.content=[pingguo,huawei,xiaomi,sanxing];

var dianshi = new Object();
dianshi.name='电视';
dianshi.content=[meidi, geli, haier, songxia,xiaomi,huawei];

var shouji = new Object();
shouji.name='手机';
shouji.content=[pingguo,huawei,xiaomi,sanxing];

var qita = new Object();
qita.name='其他';
qita.content=[qitaA];
var type_list = [[dianfengshan, kongtiao, kongqijinghuaqi], [dianbingxiang, zhilengji],
    [xiyiji,xichenqi], [diannuanqi, dianretan],[diannao,dianshi,shouji],[qita]];
function createObject(id, name) {
    var temp = new Object();
    temp.id = id;
    temp.name = name;
    return temp;
}
function getTypeList() {
    return type_list;
}