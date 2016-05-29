package com.wuwo.im.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.wuwo.im.bean.Contact;

import java.util.ArrayList;

/** 
*desc
*@author 王明远
*@日期： 2016/5/29 16:44
*@版权:Copyright 上海数慧系统有限公司  All rights reserved.
*/

public class ContactsTool {
//    ContactsListener cts;

    public ContactsTool() {
    }
    public ContactsTool(Context context) {
//        cts=ct;  , ContactsListener ct
       final Context mContext=context;
        System.out.print("获取到的联系人列表为：：：：1");
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPhoneContacts(mContext);
            }
        });
    }

    /** 联系人名称 **/
//    private ArrayList<Contact> mContacts = new ArrayList<Contact>();
    //    /** 联系人号码 **/
//     private ArrayList<String> mContactsNumber = new ArrayList<String>();
//    /** 联系人头像 **/
//    private ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();
    private static final String[] PHONES_PROJECTION = new String[] {
//            Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_ID,Phone.CONTACT_ID };
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER };
    /**联系人显示名称**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    /**电话号码**/
    private static final int PHONES_NUMBER_INDEX = 1;
    /**头像ID**/
//    private static final int PHONES_PHOTO_ID_INDEX = 2;
//    /**联系人的ID**/
//    private static final int PHONES_CONTACT_ID_INDEX = 3;

    public ArrayList<Contact> getPhoneContacts(Context mContext) {

        Log.i("ContactsTool","获取到的联系人列表为：：：：2000");
        ArrayList<Contact> mContacts = new ArrayList<Contact>();
        ContentResolver resolver = mContext.getContentResolver();
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                // 得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                // 得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
////                 得到联系人ID
//                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
//                // 得到联系人头像ID
//                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
//                // 得到联系人头像Bitamp
//                Bitmap contactPhoto = null;
//
//                // photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
//                if (photoid > 0) {
//                    Uri uri = ContentUris.withAppendedId(
//                            ContactsContract.Contacts.CONTENT_URI, contactid);
//                    InputStream input = ContactsContract.Contacts
//                            .openContactPhotoInputStream(resolver, uri);
//                    contactPhoto = BitmapFactory.decodeStream(input);
//                } else {
//                    contactPhoto = BitmapFactory.decodeResource(getResources(),
//                            R.drawable.ic_launcher);
//                }
//                mContactsName.add(contactName);
//                mContactsNumber.add(phoneNumber);
//                mContactsPhonto.add(contactPhoto);

                Contact temp=new Contact();
                temp.setContactName(contactName);
                temp.setPhoneNumber(phoneNumber);
                mContacts.add(temp);


//                Log.i("ContactsTool","获取到的联系人列表为：：：：2000"+contactName);
            }
            phoneCursor.close();
//            System.out.print("获取到的联系人列表为：：：："+mContactsNumber);
//            cts.loadContacts(mContacts);
        }
        return   mContacts;
    }











//    //查询所有联系人
//    public void testGetAll() {
//        ContentResolver resolver = mContext.getContentResolver();
//        Uri uri = Uri.parse("content://com.android.contacts/contacts");
//        Cursor idCursor = resolver.query(uri, new String[] { "_id" }, null, null, null);
//        while (idCursor.moveToNext()) {
//            //获取到raw_contacts表中的id
//            int id = idCursor.getInt(0);
//            //根据获取到的ID查询data表中的数据
//            uri = Uri.parse("content://com.android.contacts/contacts/" + id + "/data");
//            Cursor dataCursor = resolver.query(uri, new String[] { "data1", "mimetype" }, null, null, null);
//            StringBuilder sb = new StringBuilder();
//            sb.append("id=" + id);
//            //查询联系人表中的
//            while (dataCursor.moveToNext()) {
//                String data = dataCursor.getString(0);
//                String type = dataCursor.getString(1);
//                if ("vnd.android.cursor.item/name".equals(type))
//                    sb.append(", name=" + data);
//                else if ("vnd.android.cursor.item/phone_v2".equals(type))
//                    sb.append(", phone=" + data);
//                else if ("vnd.android.cursor.item/email_v2".equals(type))
//                    sb.append(", email=" + data);
//            }
//            System.out.println(sb);
//        }
//    }

}