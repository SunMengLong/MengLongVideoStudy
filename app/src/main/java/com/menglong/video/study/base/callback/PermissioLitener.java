package com.menglong.video.study.base.callback;

import java.util.ArrayList;

/**
 * Created by sml on 2019/8/7.
 * 动态权限回调接口
 */
public interface PermissioLitener {
    //申请的所有权限都通过了所执行的方法
    void onGranted();
    //申请的权限有的失败了，将申请失败的权限放进集合作为参数提示用户
    void onDenied(ArrayList<String> list);
}
