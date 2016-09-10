//package com.wuwo.im.fragement;
//
//import android.annotation.SuppressLint;
//import android.net.Uri;
//import android.view.View;
//
//import com.facebook.drawee.view.SimpleDraweeView;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.wuwo.im.adapter.CommRecyclerAdapter;
//import com.wuwo.im.adapter.CommRecyclerViewHolder;
//import com.wuwo.im.bean.newsMessage;
//import com.wuwo.im.config.WowuApp;
//import com.zhy.http.okhttp.OkHttpUtils;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import im.wuwo.com.wuwo.R;
//
///**
// * 首页 设置 fragment
// * @author dewyze
// */
//
//@SuppressLint("ValidFragment")
//public class Portal_XiaoXiFragment extends BasePortal_TabFragment {
//
//    private ArrayList<newsMessage> meeting_userlist = new ArrayList<newsMessage>(); //记录所有的最新消息
//    @Override
//    public void setLoadInfo(String totalresult) throws JSONException {
//
//
//        Gson gson = new GsonBuilder().create();
//        if (totalresult != null) {
//            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<newsMessage>>() {
//            }.getType();
//            meeting_userlist = gson.fromJson(totalresult, type);
//        }
//
////        if (totalresult != null) {
////            //                    int size = result.getPropertyCount();
////            JSONObject obj = new JSONObject(totalresult);
////            if (obj.optBoolean("success")) {
////                meeting_userlist.clear();
////                String result = obj.optString("result");
////                //Log.d("收文获得的返回值列表为：",result + "");
////                if (result != null) {
////                    JSONArray attachlist = new JSONArray(result);
////                    for (int i = 0; i < attachlist.length(); i++) {
////                        JSONObject attachjson = attachlist.getJSONObject(i);
////                        newsMessage tempmail = new newsMessage();
////                        tempmail.setId(attachjson.optString("id"));
////                        tempmail.setTitle(attachjson.optString("title"));
////                        tempmail.setContent(attachjson.optString("name"));
////                        tempmail.setTime0(attachjson.optString("publishdate"));
////                        tempmail.setCreater(attachjson.optString("typename"));
////                        tempmail.setProjectId(attachjson.optString("id"));
////                        tempmail.setWidgetsLabel(attachjson.optString("typename"));
////                        tempmail.setShowurl(attachjson.optString("extension"));
////                        meeting_userlist.add(tempmail);
////                    }
////                }
////            }
////        }
//    }
//
//    @Override
//    public ArrayList<?> getLoadInfo() {
////        ArrayList<newsMessage> temp = (ArrayList<newsMessage>) meeting_userlist.clone();
////        return temp;
//
//        return meeting_userlist;
//    }
//
//    @Override
//    public CommRecyclerAdapter initAdapter() {
//        messageRAdapter = new CommRecyclerAdapter<newsMessage>(getActivity(), R.layout.item_xiaoxi_view) {
//            @Override
//            public void convert(CommRecyclerViewHolder viewHolder, newsMessage mainMessage) {
//                //对对应的View进行赋值
////                viewHolder.setText(R.id.news_title, mainMessage.getTitle());
//
//                SimpleDraweeView portal_news_img = (SimpleDraweeView) viewHolder.getView(R.id.news_label_pic);
//                portal_news_img.setImageURI(Uri.parse("http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg"));
//            }
//
//            @Override
//            public int getItemViewType(int position) {
//                return super.getItemViewType(position);
//            }
//        };
//        //设置item点击事件
//        messageRAdapter.setOnItemClick(new CommRecyclerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//
///*                Intent intent2 = new Intent(mContext, ChatListActivity.class);
//                //        intent2.putExtra("content", newsMessagelist.get(tempPosition-1).getContent());
////                intent2.putExtra("url", DistApp.serverAbsolutePath + "/snews!mobileNewsdetail.action?news.id=4028816f4d4be502014d4c0e22dc003d");
////                intent2.putExtra("name", "消息通知");
//                startActivity(intent2);
//                mContext.overridePendingTransition(0, 0);*/
//            }
//        });
//        return null;
//    }
//
////    @Override
////    public PostFormBuilder httpBuilder() {
////        return  OkHttpUtils
////                .post()
//////                .url(WowuApp.serverAbsolutePath)
////                .url("http://58.246.138.178:8000/DistMobile/mobileMeeting!getAllMeeting.action")
////                .addParams("type", "smartplan")
////                .addParams("newsMessageId", "4028826f505a3b0f01506553b0c80c3a")
////                .addParams("action", "getlawrulelist");
////    }
//
//
//
//
//    @Override
//    public JSONObject postJsonObject() throws JSONException {
//        JSONObject json = new JSONObject();
//        json.put("PhoneNumber", WowuApp.PhoneNumber);
//        json.put("Type", "0");
//        return json;
//    }
//
//    @Override
//    public String postURL() {
//        return "http://58.246.138.178:8000/DistMobile/mobileMeeting!getAllMeeting.action";
//    }
//
//    @Override
//    public String getURL() {
//        return OkHttpUtils.GetUserInfoURL+"?userId="+WowuApp.UserId;
//    }
//
//
//
//
//
//
//    @Override
//    public boolean loadMore() {
////        return false;
//        return true;
//    }
//
//    @Override
//    public String getFragmentName() {
//        return "Portal_XiaoXiFragment";
//    }
//
//
//}
