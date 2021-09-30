"use strict"


//采纳live-server 设置全局变量设置, 增加逻辑处理,便于兼容标准开发模式打包的容错
let _lURL_Href_INFO = window.location.href;
if (_lURL_Href_INFO.indexOf("http://127.0.0.1:8099") >= 0) {
    axios.defaults.baseURL = "http://127.0.0.1:6611/";
}

/**
 * 请求服务端拦截器和过滤拦截器的全局处理
 */
//增加拦截器
axios.interceptors.request.use(
    config => {
        //为每个http header都加上token
        config.headers.token = myUtil.getToken();
        return config;
    },
    err => {
        return Promise.reject(err);
    });

//添加响应拦截器
axios.interceptors.response.use(function (response) {
    //对响应数据做些事
    if (response.data.ret=="ERROR_TOKEN") {
        logoutToRedict();
        return;
    }
    return response;
}, function (error) {
    console.log(error);
    //请求错误时做些事
    return Promise.reject(error);
});

function logoutToRedict() {
	myUtil.setToken("");
	myUtil.setPmAgent("");
    //跳转至登录页面
    var vUlr=window.location.protocol+"//"+window.location.host;
    top.location.href =vUlr;// "index.html";
}
