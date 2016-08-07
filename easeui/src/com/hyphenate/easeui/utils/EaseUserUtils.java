package com.hyphenate.easeui.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.domain.EaseUser;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.service.LoadserverdataService;
import com.zhy.http.okhttp.service.loadServerDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class EaseUserUtils {
    
    static EaseUserProfileProvider userProvider;
    
    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }
    
    /**
     * get EaseUser according username
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username){
        if(userProvider != null)
            return userProvider.getUser(username);
        
        return null;
    }
    
    /**
     * set user avatar
     * @param username
     */
    public static void setUserAvatar(final Context context, final String username, final ImageView imageView){

       if( EaseImageUtils.usersPhotoUrl.get(username) !=null){
           Glide.with(new WeakReference<Context>(context).get()).load(Uri.parse( EaseImageUtils.usersPhotoUrl.get(username))).into(imageView);
       }else{
           new LoadserverdataService(new loadServerDataListener(){
               @Override
               public void loadServerData(String response, int flag) {
                   try {
                       JSONObject temp=new JSONObject(response);
                       if(temp!=null){
                           EaseImageUtils.usersNickName.put(username,temp.optString("Name"));

                           JSONObject icon=new JSONObject( temp.optString("Icon"));
                           if(icon!=null){
//                                    * Icon : {"Url":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/35b5090b-aaf4-4c6d-b46f-e42073e11f4ex128.jpg","FullUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/35b5090b-aaf4-4c6d-b46f-e42073e11f4e.jpg","Id":"dc3df506-e657-4643-bdbc-71b09e7b1afe","IsIcon":true}
                                if(context!=null && new WeakReference<Context>(context).get() !=null){
                                    try{
                                        Glide.with(new WeakReference<Context>(context).get()).load(Uri.parse( icon.optString("FullUrl"))).into(imageView);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                               EaseImageUtils.usersPhotoUrl.put(username,icon.optString("FullUrl"));
                               return;
                           }
                       }
//                       Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);

                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }

               @Override
               public void loadDataFailed(String response, int flag) {
               }
           }) .loadGetJsonRequestData( OkHttpUtils.GetUserInfoURL+"?userId=" + username ,0);

       }

//        Glide.with(context).load(Uri.parse("http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg")).into(imageView);

//    	EaseUser user = getUserInfo(username);
//        if(user != null && user.getAvatar() != null){
//            try {
//                int avatarResId = Integer.parseInt(user.getAvatar());
//                Glide.with(context).load(avatarResId).into(imageView);
//            } catch (Exception e) {
//                //use default avatar
//                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).into(imageView);
//            }
//        }else{
//            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
//        }
    }







    
    /**
     * set user's nickname
     */
    public static void setUserNick(String username,TextView textView){
        if(textView != null){
        	EaseUser user = getUserInfo(username);
        	if(user != null && user.getNick() != null){
        		textView.setText(user.getNick());
        	}else{
        		textView.setText(  EaseImageUtils.usersNickName.get(username)!=null ?  EaseImageUtils.usersNickName.get(username):username);
        	}
        }
    }

    public static void setUserShowName(final Context context, final String username, final TextView textView) {

        if( EaseImageUtils.usersNickName.get(username) !=null){
            textView.setText( EaseImageUtils.usersNickName.get(username));
        }else{
            new LoadserverdataService(new loadServerDataListener(){
                @Override
                public void loadServerData(String response, int flag) {
                    try {
                        JSONObject temp=new JSONObject(response);
                        if(temp!=null){
                            EaseImageUtils.usersNickName.put(username,temp.optString("Name"));
                            textView .setText( EaseImageUtils.usersNickName.get(username));
                            JSONObject icon=new JSONObject( temp.optString("Icon"));
                            if(icon!=null){
                                  EaseImageUtils.usersPhotoUrl.put(username,icon.optString("FullUrl"));
                             }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void loadDataFailed(String response, int flag) {
                }
            }) .loadGetJsonRequestData( OkHttpUtils.GetUserInfoURL+"?userId=" + username ,0);

        }



    }
}
