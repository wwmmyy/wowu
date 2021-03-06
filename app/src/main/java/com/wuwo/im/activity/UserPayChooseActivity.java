package com.wuwo.im.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.AliPayResult;
import com.wuwo.im.util.AliPaySignUtils;
import com.wuwo.im.util.MyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import im.imxianzhi.com.imxianzhi.R;

public class UserPayChooseActivity extends BaseLoadActivity implements IWXAPIEventHandler {

    public static final String TAG = "UserPayChooseActivity";
    ImageView iv_wehcatpay, iv_alipay, viptype;
    boolean DEFAULT_PAY_WECHAT = true;
    TextView tv_vip_intro, tv_vip_money,btn_confirm;
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI wxApi;
    private int vipType;
    private  String payAcount="0.01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pay_choose);
        initView();

    }

    private void initView() {
        findViewById(R.id.return_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.top_title)).setText("支付");
        btn_confirm=(TextView)findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        findViewById(R.id.ln_wechatpay).setOnClickListener(this);
        findViewById(R.id.ln_alipay).setOnClickListener(this);


        iv_wehcatpay = (ImageView) findViewById(R.id.iv_wehcatpay);
        iv_alipay = (ImageView) findViewById(R.id.iv_alipay);
        viptype = (ImageView) findViewById(R.id.viptype);
        tv_vip_intro = (TextView) findViewById(R.id.tv_vip_intro);
        tv_vip_money = (TextView) findViewById(R.id.tv_vip_money);


        payAcount= getIntent().getStringExtra("money");
        tv_vip_intro.setText(getIntent().getStringExtra("intro"));
        tv_vip_money.setText(payAcount);
        btn_confirm.setText("确认支付¥"+payAcount);


        vipType = getIntent().getIntExtra("vipType", 1);
        switch (vipType) {
            case 5:
                viptype.setImageResource(R.drawable.svip_b);
                break;
            case 4:
                viptype.setImageResource(R.drawable.vip_b);
                break;
            case 3:
                viptype.setImageResource(R.drawable.month6);
                break;
            case 2:
                viptype.setImageResource(R.drawable.month3);
                break;
            case 1:
                viptype.setImageResource(R.drawable.month1);
                break;
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.ln_wechatpay:

                iv_wehcatpay.setImageResource(R.drawable.choose);
                iv_alipay.setImageResource(R.drawable.unchoose);
                DEFAULT_PAY_WECHAT = true;
                break;
            case R.id.ln_alipay:
                iv_wehcatpay.setImageResource(R.drawable.unchoose);
                iv_alipay.setImageResource(R.drawable.choose);
                DEFAULT_PAY_WECHAT = false;
                break;
            case R.id.btn_confirm:
                startPay();
                break;

        }
    }

    private void startPay() {

        if (DEFAULT_PAY_WECHAT) {
            MyToast.show(mContext, "启动微信支付功能");
            payWeChat();
        } else {
            payAliPay();
        }

    }


    //    阿里支付
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 商户PID
    public static final String PARTNER = "2088221946008246";
    // 商户收款账号
    public static final String SELLER = "kevinchen@imxianzhi.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAL9c0sQaOTguW2GlR4lb7iL8i34Je+oHHKz1a7FhtGSBKPz96H9QCoPbZrP0CuxxIyQ4glPoaBYt9ltfbeb0y6j1xiNKDSKSijh43opKpQN+euL5+AS18a57wpT3c5GdmbKMIMUF+00+/oK6jrmhrl/rCGVHPhI3kdvNzh4yykeTAgMBAAECgYBpZzVocpFF0rLey+zlSc+XUcd7urmYJCa1VdDluU1ldocfIrPnPC0uh7DAOhGyPnb+wml20jJS1So3Fg8xAXETQcWxmN5vS6UhJQbWGYe8ipARGfA47ZJTh6B+OaPiGUxwH84F2ua30uW9ZdPyFhtGOSijN8bCKWpaR6KXRH26AQJBAN+oFGEf2OaidjMBWhITVu5d7qttfZdTmJjdcXS2gUZP67fzPcAAG5mtfS6jP9igMfrM3Ti8ezBadSWIYgUomoECQQDbCTLY3u7L6iocKhHZ7EdKMqEXbawVY6fHff8dVtDnSmQCh2JN5+KIRYREGULS0Xij8m0tAErnTLT6s41oPtATAkAT+zEu6Ch5mmQS9Hj2inHGw++Rsyt4PIiyh34eXju4a2V0vq9yms/cb+pyIzWOBenRLUJKUlRzInG6069mJJWBAkEApmqxwEZ5BmMuhWGNWJaFf6FB9av0//dMZWycZS0t4V0K6UT1cp2I5uJyYM8Uj1ppn560Rmmff2cvZf7/pEF9EwJAThsex4+AceHpZfOYrzT1cGKe19KWxHyqeylybEcqmtR+fBF61Lm+PAFhc2I5QPXqpiTKTDYs1s2XaVf0wsM41Q==";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "";
    private static final int SDK_PAY_FLAG = 1;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    AliPayResult payResult = new AliPayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(UserPayChooseActivity.this, "支付成功",
                                Toast.LENGTH_SHORT).show();

                        Intent intent2 = new Intent(mContext, UserPaySuccessActivity.class);
                        intent2.putExtra("vipType", vipType);
//                        startActivity(intent2);
                        startActivityForResult(intent2, WowuApp.ALIPAY);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(UserPayChooseActivity.this, "支付结果确认中",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(UserPayChooseActivity.this, "支付失败",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


    private void payAliPay() {
        if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE)
                || TextUtils.isEmpty(SELLER)) {
            new AlertDialog.Builder(this)
                    .setTitle("警告")
                    .setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface, int i) {
                                    //
                                    finish();
                                }
                            }).show();
            return;
        }

//向自己服务器发送支付信息，此举可能没有，后面再验证屏蔽掉
        try {
            JSONObject json = new JSONObject();
            json.put("VipType", vipType);
            json.put("PaymentMethod", "alipay");
            loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.OrderBuyURL, json.toString(), R.id.ln_alipay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }

//	/**
//	 * 原生的H5（手机网页版支付切natvie支付） 【对应页面网页支付按钮】
//	 *
//	 * @param v
//	 */
//	public void h5Pay(View v) {
//		Intent intent = new Intent(this, AliPayH5PayDemoActivity.class);
//		Bundle extras = new Bundle();
//		/**
//		 * url是测试的网站，在app内部打开页面是基于webview打开的，demo中的webview是H5PayDemoActivity，
//		 * demo中拦截url进行支付的逻辑是在H5PayDemoActivity中shouldOverrideUrlLoading方法实现，
//		 * 商户可以根据自己的需求来实现
//		 */
//		String url = "http://m.taobao.com";
//		// url可以是一号店或者淘宝等第三方的购物wap站点，在该网站的支付过程中，支付宝sdk完成拦截支付
//		extras.putString("url", url);
//		intent.putExtras(extras);
//		startActivity(intent);
//	}

    /**
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(String subject, String body,String orderId, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" +orderId + "\"";//  getOutTradeNo()

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

//    /**
//     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
//     */
//    private String getOutTradeNo() {
//        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
//                Locale.getDefault());
//        Date date = new Date();
//        String key = format.format(date);
//
//        Random r = new Random();
//        key = key + r.nextInt();
//        key = key.substring(0, 15);
//        return key;
//    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private String sign(String content) {
        return AliPaySignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null && resultCode == RESULT_OK) {
            Bundle result = data.getExtras(); //data为B中回传的Intent
            switch (requestCode) {
                case WowuApp.ALIPAY:
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("payState", result.getBoolean("payState"));//给 bundle 写入数据
                    Intent mIntent = new Intent();
                    mIntent.putExtras(bundle);
                    setResult(RESULT_OK, mIntent);
                    finish();//此处一定要调用finish()方法
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;

            }
        }
    }


    //微信支付
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void payWeChat() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        wxApi = WXAPIFactory.createWXAPI(this, WowuApp.WeChat_APP_ID, false);
        // 将该app注册到微信
        wxApi.registerApp(WowuApp.WeChat_APP_ID);

        if (!wxApi.isWXAppSupportAPI()) {
            MyToast.show(mContext, "当前版本不支持支付功能");
            return;
        }
//发起支付的流程，提交订单到自己服务器，自己服务器返回给我们微信支付的参数后，我们就可以愉快的支付了
        try {
            JSONObject json = new JSONObject();
            json.put("VipType", vipType);
            json.put("PaymentMethod", "wechat");
            loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.OrderBuyURL, json.toString(), R.id.ln_wechatpay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {


    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
        String msg = "";
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

            if (resp.errCode == 0) {
                msg = "支付成功";
                Intent intent2 = new Intent(mContext, UserPaySuccessActivity.class);
                intent2.putExtra("vipType", vipType);
//                        startActivity(intent2);
                startActivityForResult(intent2, WowuApp.ALIPAY);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else if (resp.errCode == -1) {
                msg = "已取消支付";
            } else if (resp.errCode == -2) {
                msg = "支付失败";
            }
            MyToast.show(mContext, msg);
        }
//        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//
//            new EaseAlertDialog(this, null, "支付结果", null, new EaseAlertDialog.AlertDialogUser() {
//                @Override
//                public void onResult(boolean confirmed, Bundle bundle) {
//                    if (confirmed) {
//                        finish();
//                    }
//                }
//            }, true).show();
//        }


    }


    @Override
    public void loadServerData(String response, int flag) {

        switch (flag) {

            case R.id.ln_alipay:

//            {"OrderId":"d5c3b03f-fd26-4e22-8c4f-c89a5f5fec14","OrderNumber":"1608061128489042","Total":0.01,"PrepayId":"1608061128489042","Wechat":null}
                Log.i(TAG,response);

                try {
                    JSONObject   jsonAlipay = new JSONObject(response);
                    String orderInfo = getOrderInfo("先知会员","先知会员Android端支付",jsonAlipay.optString("OrderId"), jsonAlipay.optString("Total") );//"先知会员Android端支付"  payAcount

                    /**
                     * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
                     */
                    String sign = sign(orderInfo);
                    try {
//                        仅需对sign 做URL编码
                        sign = URLEncoder.encode(sign, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    /**
                     * 完整的符合支付宝参数规范的订单信息
                     */
                    final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                            + getSignType();

                    Runnable payRunnable = new Runnable() {

                        @Override
                        public void run() {
                            // 构造PayTask 对象
                            PayTask alipay = new PayTask(UserPayChooseActivity.this);
                            // 调用支付接口，获取支付结果
                            String result = alipay.pay(payInfo, true);
                            Message msg = new Message();
                            msg.what = SDK_PAY_FLAG;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    };
                    // 必须异步调用
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();

                } catch (JSONException e) {
                    MyToast.show(mContext, "服务器返回值异常");
                    e.printStackTrace();
                }

                break;
            case R.id.ln_wechatpay:
//    return {"OrderId":"","OrderNumber":"","Total":0,"PrepayId":"",
// "Wechat":{"AppId":"","PartnerId":"","PrepayId":"","PackageValue":"","NonceStr":"","TimeStamp":"1461397867","Sign":""}}

                Log.i(TAG,response);
//              解析自己服务器返回的数据后向微信服务器发起支付请求
                JSONObject json0 = null;
                try {
                    json0 = new JSONObject(response);
                    PayReq req = new PayReq();
//                    req.appId = WowuApp.WeChat_APP_ID;
                    req.prepayId = json0.optString("PrepayId");

                    JSONObject json = new JSONObject(json0.optString("Wechat"));
                    req.appId = json.optString("AppId");
                    req.partnerId = json.getString("PartnerId");
                    req.prepayId = json.getString("PrepayId");
                    req.nonceStr = json.getString("NonceStr");
                    req.timeStamp = json.getString("TimeStamp");
                    req.packageValue = json.getString("PackageValue");
                    req.sign = json.getString("Sign");
                    req.extData = "android pay"; // optional
                    wxApi.sendReq(req);

                } catch (JSONException e) {
                    MyToast.show(mContext, "服务器返回值异常");
                    e.printStackTrace();
                }
                break;
        }


    }

    @Override
    public void loadDataFailed(String response, int flag) {
        MyToast.show(mContext, response);
    }


}
