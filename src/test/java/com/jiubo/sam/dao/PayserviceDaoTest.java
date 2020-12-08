package com.jiubo.sam.dao;

import org.junit.Test;


/**
 * @desc:
 * @date: 2019-09-09 16:09
 * @author: dx
 * @version: 1.0
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class PayserviceDaoTest {

//    @Autowired
//    private PayserviceDao payserviceDao;

    @Test
    public void addPayservice() {
//        PayserviceBean payserviceBean = new PayserviceBean();
//        payserviceBean.setName("测试");
//        payserviceBean.setIsuse("0");
//        System.out.println("identity:" + payserviceBean.getPayserviceId());
    }

    @Test
    public void addPayserviceList() {
//        List<PayserviceBean> payserviceBeans = new ArrayList<PayserviceBean>();
//        for (int i = 0; i < 5; i++) {
//            PayserviceBean payserviceBean = new PayserviceBean();
//            payserviceBean.setName("测试" + i);
//            payserviceBean.setIsuse("0");
//            payserviceBeans.add(payserviceBean);
//        }


    }

    public static void main(String[] args) {
        double d = 201 / 100;
        System.out.println(d);
        // System.out.println(Double.valueOf("16.0").intValue());
        StringBuffer bufferH = new StringBuffer();
        bufferH.append("CASE WHEN CONVERT(VARCHAR(100), GETDATE(), 23) > MAX(CASE E.PAYSERVICE_ID WHEN ").append(" #ID ").append(" THEN CONVERT(VARCHAR(100), E.ENDTIME, 23) ELSE NULL END) THEN ");
        bufferH.append("(DATEDIFF(MONTH, MAX(CASE E.PAYSERVICE_ID WHEN ").append(" #ID ").append(" THEN E.ENDTIME ELSE NULL END), GETDATE())) * MAX(CASE E.PAYSERVICE_ID WHEN ")
                .append(" #ID ").append(" THEN E.RECEIVABLE ELSE 0 END) ELSE 0 END ").append("qianKuan");
        System.out.println(bufferH.toString());

    }
}