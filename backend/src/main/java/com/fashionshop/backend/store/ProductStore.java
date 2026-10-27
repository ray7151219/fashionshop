package com.fashionshop.backend.store;

import com.fashionshop.backend.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 商品数据仓库。
 * 当前为内存固定数据，接入数据库时只需替换这一层。
 */
public final class ProductStore {

    private static final List<Product> PRODUCTS = createProducts();

    private ProductStore() {
    }

    public static List<Product> getProducts() {
        return PRODUCTS;
    }

    public static Product findProduct(long id) {
        for (Product product : PRODUCTS) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }

    private static List<Product> createProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "酒红色长款连衣裙",  "裙子", 299.00,
                "https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=600&q=80&auto=format&fit=crop",
                "酒红色垂坠面料，收腰大摆设计，走动时裙摆轻盈飘逸，适合约会、晚宴与正式场合。"));
        products.add(new Product(2, "复古水洗牛仔裤", "裤子", 239.00,
                "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=600&q=80&auto=format&fit=crop",
                "经典水洗做旧工艺，修身直筒版型，膝盖破洞设计带点街头感，日常百搭。"));
        products.add(new Product(3, "波点印花衬衫",  "上衣", 259.00,
                "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&q=80&auto=format&fit=crop",
                "深蓝色底配白色波点，翻领单排扣设计，面料柔软透气，通勤或休闲都好搭。"));
        products.add(new Product(4, "猫咪印花短袖T恤", "上衣", 129.00,
                "https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=600&q=80&auto=format&fit=crop",
                "纯棉面料，宽松圆领版型，蓝色猫咪印花俏皮有趣，夏季百搭单品。"));
        products.add(new Product(5, "黑色A字半身裙",  "裙子", 259.00,
                "https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=600&q=80&auto=format&fit=crop",
                "高腰 A 字剪裁，裙摆自然展开修饰胯部线条；黑色百搭，配衬衫或针织衫都合适。"));
        products.add(new Product(6, "焦糖色飞行夹克",  "外套", 499.00,
                "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=600&q=80&auto=format&fit=crop",
                "焦糖色光泽面料，棒球领与罗纹收口设计，版型利落，春秋季通勤休闲皆宜。"));
        products.add(new Product(7, "经典格纹羊绒围巾",  "配饰", 189.00,
                "https://images.unsplash.com/photo-1520903920243-00d872a2d1c9?w=600&q=80&auto=format&fit=crop",
                "细腻羊绒混纺，触感柔软保暖；经典格纹设计，可做围巾也可做披肩。"));
        products.add(new Product(8, "复古休闲运动鞋",  "鞋子", 329.00,
                "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=600&q=80&auto=format&fit=crop",
                "复古拼接鞋面，橡胶大底防滑耐磨；焦糖棕配色低调耐看，四季百搭。"));
        products.add(new Product(9, "纯棉基础款白衬衫",  "上衣", 219.00,
                "https://images.unsplash.com/photo-1603252109303-2751441dd157?w=600&q=80&auto=format&fit=crop",
                "挺括纯棉面料，经典基础版型，可单穿可内搭，是衣橱里的百搭单品。"));
        products.add(new Product(10, "条纹慵懒针织衫",  "上衣", 269.00,
                "https://images.unsplash.com/photo-1517677208171-0bc6725a3e60?w=600&q=80&auto=format&fit=crop",
                "柔软针织面料，宽松落肩设计，蓝白条纹清新耐看，春秋单穿或叠穿都合适。"));
        products.add(new Product(11, "宽松连帽卫衣",  "上衣", 199.00,
                "https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=600&q=80&auto=format&fit=crop",
                "加绒内里，宽松落肩版型，连帽抽绳设计，舒适保暖又有街头感。"));
        products.add(new Product(12, "高腰阔腿牛仔裤",  "裤子", 279.00,
                "https://images.unsplash.com/photo-1542272604-787c3835535d?w=600&q=80&auto=format&fit=crop",
                "高腰阔腿版型，垂坠挺括，修饰腿型显腿长，复古蓝水洗百搭。"));
        products.add(new Product(13, "黑色修身九分西裤",  "裤子", 259.00,
                "https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?w=600&q=80&auto=format&fit=crop",
                "修身九分剪裁，垂顺抗皱面料，通勤商务与休闲场合都能驾驭。"));
        products.add(new Product(14, "卡其色休闲工装裤", "裤子", 229.00,
                "https://images.unsplash.com/photo-1517438476312-10d79c077509?w=600&q=80&auto=format&fit=crop",
                "宽松直筒工装版型，多口袋设计，卡其色耐看实用，日常户外都好穿。"));
        products.add(new Product(15, "碎花雪纺连衣裙",  "裙子", 329.00,
                "https://images.unsplash.com/photo-1612336307429-8a898d10e223?w=600&q=80&auto=format&fit=crop",
                "轻盈雪纺面料，小碎花清新浪漫，收腰长裙摆，度假与约会都合适。"));
        products.add(new Product(16, "紫色露肩连衣裙",  "裙子", 309.00,
                "https://images.unsplash.com/photo-1566174053879-31528523f8ae?w=600&q=80&auto=format&fit=crop",
                "柔软面料舒滑质感，露肩设计减龄俏皮，适合在宴会活动穿。"));
        products.add(new Product(17, "米色中长款风衣",  "外套", 559.00,
                "https://images.unsplash.com/photo-1539533018447-63fcce2678e3?w=600&q=80&auto=format&fit=crop",
                "经典双排扣风衣版型，中长款剪裁，米色百搭显气质，春秋通勤首选。"));
        products.add(new Product(18, "绿棕色夹克外套",  "外套", 699.00,
                "https://images.unsplash.com/photo-1544022613-e87ca75a784a?w=600&q=80&auto=format&fit=crop",
                "真材实料，百搭外套，轻便防风，春秋必备。"));
        products.add(new Product(19, "棕灰色百搭帆布鞋",  "鞋子", 199.00,
                "https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=600&q=80&auto=format&fit=crop",
                "棕灰色帆布鞋面，橡胶鞋底，轻便透气，几乎能搭配所有日常造型。"));
        products.add(new Product(20, "蓝色绣花高跟鞋",  "鞋子", 459.00,
                "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=600&q=80&auto=format&fit=crop",
                "高端大气上档次，充满中国古色绣花搭配灵感，蓝色搭配荷花显质感。"));
        products.add(new Product(21, "羊毛贝雷帽",  "配饰", 159.00,
                "https://images.unsplash.com/photo-1521369909029-2afed882baee?w=600&q=80&auto=format&fit=crop",
                "细软羊毛材质，经典贝雷帽型，保暖有型，点亮整体穿搭的亮点单品。"));
        products.add(new Product(22, "帆布单肩托特包",  "配饰", 239.00,
                "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600&q=80&auto=format&fit=crop",
                "厚实帆布面料，大容量托特包型，轻便耐用，通勤上学都实用。"));
        return Collections.unmodifiableList(products);
    }
}
