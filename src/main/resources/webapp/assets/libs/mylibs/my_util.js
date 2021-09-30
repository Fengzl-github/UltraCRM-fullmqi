'use strict'

var myUtil = (function () {

    //设置Cookie,expires-过期时间,单位:秒   （以前是小时）
    function setCookie(name, value, expires) {
        if ((expires == null) || (expires == ""))
            //    expires = (30 * 24 ); //默认值30天 
            expires = (30 * 24 * 3600); //默认值30天 

        var exp = new Date();
        //exp.setTime(exp.getTime() + (3600 * 1000 * expires));  //Cookie最小单位ms小时
        exp.setTime(exp.getTime() + (1000 * expires));

        //如果cookie中要想存中文，不用escape(value)读出来会是乱码
        value = escape(value);
        document.cookie = name + "=" + value + "; expires=" + exp.toGMTString() + "; path=/";
    }

    function getCookie(name) {
        var search;
        search = name + "="
        var offset = document.cookie.indexOf(search)
        if (offset != -1) {
            offset += search.length;
            end = document.cookie.indexOf(";", offset);
            if (end == -1)
                end = document.cookie.length;
            return unescape(document.cookie.substring(offset, end));
        }
        else
            return "";
    }
    function deleteCookie(name) {
        var expdate = new Date();
        expdate.setTime(expdate.getTime() - 1000);
        var strVal = getCookie(name);
        if (strVal != null)
            document.cookie = name + "=" + strVal + ";expires=" + expdate.toGMTString() + "; path=/";
    }


    //2015-07-20 为了安全和性能考虑，以后保存变量不再使用Cookie，改用HTML5 的 localStorage
    //设置Cookie,expires-过期时间,单位:小时（暂不实现）
    function setLookie(name, value, expires) {
        //if (typeof (window.localStorage) == "undefined")
        var isSupportLocalStorage = (('localStorage' in window) && (window['localStorage'] !== null));
        //支持本地存储的浏览器：IE8+、Firefox3.0+、Opera10.5+、Chrome4.0+、Safari4.0+、iPhone2.0+、Andrioid2.0+
        if (isSupportLocalStorage == true) {
            //如果cookie中要想存中文，不用escape(value)读出来会是乱码
            value = escape(value);
            window.localStorage.setItem(name, value);
        }
        else {
            setCookie(name, value, expires);
        }
    }

    function getLookie(name) {
        var isSupportLocalStorage = (('localStorage' in window) && (window['localStorage'] !== null));
        if (isSupportLocalStorage == true) {
            var result = window.localStorage.getItem(name);
            if (result == null) result = "";
            else result = unescape(result);
            if (result == "") result = getCookie(name); //2015-07-21 peng 临时使用，为了与以前版本兼容
            return result;
        }
        else {
            return getCookie(name);
        }
    }

    function deleteLookie(name) {
        var isSupportLocalStorage = (('localStorage' in window) && (window['localStorage'] !== null));
        if (isSupportLocalStorage == true) {
            var result = window.localStorage.removeItem(name);
        }
        else {
            return deleteCookie(name);
        }
    }


    //用javascript获取url参数的方法-多浏览器兼容
    var fun_querystring = function (strName) {
        var strReturn = "";
        var reg = new RegExp("(^|&)" + strName + "=([^&]*)(&|$)", "i");
        var strRet = window.location.search.substr(1).match(reg);
        //if (strRet != null) strReturn = unescape(strRet[2]);
        if ((strRet != null) && (strRet.length >= 2)) {
            if (strRet[2].indexOf("%") > 0)
                strReturn = decodeURIComponent(strRet[2]);
            else
                strReturn = strRet[2];
        }
        return strReturn;
    }

    //记忆账号密码
    var setAccountPwd = function (strValue) {
        setLookie("myToken_account_pwd", strValue, 3600 * 24);
    }

    var getAccountPwd = function () {
        return getLookie("myToken_account_pwd");
    }

    //设置登录信息
    var setPmAgent = function (strValue) {
        setLookie("myToken_Key_agent", strValue, 3600 * 24);
    }

    var getPmAgent = function () {
        return getLookie("myToken_Key_agent");
    }

    //设置TOKEN存储
    var setToken = function (strToken) {
        set("myToken_Key_jwt", strToken, 3600 * 24);
    }

    var getToken = function () {
        return get("myToken_Key_jwt");
    }

    //设置自定义主题颜色
    var setMyTheme = function (strColor) {
        setLookie("myTheme_Key_jwt", strColor, 3600 * 24);
    }

    var getMyTheme = function () {
        return getLookie("myTheme_Key_jwt");
    }

    var nSupport = window.sessionStorage;
    var set = function (strKey, strValue) {
        if (nSupport == true)
            window.sessionStorage.setItem(strKey, strValue);
        else
            setLookie(strKey, strValue, 24);
    }
    var get = function (strKey) {
        if (nSupport == true)
            return window.sessionStorage.getItem(strKey);
        else
            return getLookie(strKey);
    }

    var remove = function (strKey) {
        if (nSupport == true)
            window.sessionStorage.removeItem(strKey);
        else
            return deleteLookie(strKey);
    }



    return {
        setAccountPwd:setAccountPwd,
        getAccountPwd:getAccountPwd,
        setToken: setToken,
        getToken: getToken,
        getPmAgent: getPmAgent,
        setPmAgent: setPmAgent,
        setMyTheme: setMyTheme,
        getMyTheme: getMyTheme,
        setCookie: setCookie,
        getCookie: getCookie,
        deleteCookie: deleteCookie,
        setLookie: setLookie,
        getLookie: getLookie,
        deleteLookie: deleteLookie,
        fun_querystring: fun_querystring,
        base64encode: function (input) {
            var base64 = new Base64();
            return base64.encode(input);
        },
        base64decode: function (input) {
            var base64 = new Base64();
            return base64.decode(input);
        }
    };
})();

function Base64() {
    var _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    // base64编码
    this.encode = function (input) {
        var output = "";
        var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
        var i = 0;

        input = _utf8_encode(input);

        while (i < input.length) {

            chr1 = input.charCodeAt(i++);
            chr2 = input.charCodeAt(i++);
            chr3 = input.charCodeAt(i++);

            enc1 = chr1 >> 2;
            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
            enc4 = chr3 & 63;

            if (isNaN(chr2)) {
                enc3 = enc4 = 64;
            } else if (isNaN(chr3)) {
                enc4 = 64;
            }

            output = output + _keyStr.charAt(enc1) + _keyStr.charAt(enc2) + _keyStr.charAt(enc3) + _keyStr.charAt(enc4);
        }

        return output;
    }

    this.decode = function (input) {
        var chr1, chr2, chr3;
        var enc1, enc2, enc3, enc4;
        var i = 0;

        var myReg = new RegExp(/^[a-zA-Z0-9+\/=]{4,}$/g); //base64编码
        if (myReg.test(input) == false) return input;
        //特征：编码后的字符串长度为4的倍数，不能全是数字
        myReg = new RegExp(/^[0-9]+$/g); //base64编码
        if (myReg.test(input) == true) return input;
        if ((input.length % 4) != 0) return input;

        var output = "";
        //input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
        while (i < input.length) {

            enc1 = _keyStr.indexOf(input.charAt(i++));
            enc2 = _keyStr.indexOf(input.charAt(i++));
            enc3 = _keyStr.indexOf(input.charAt(i++));
            enc4 = _keyStr.indexOf(input.charAt(i++));

            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;

            output = output + String.fromCharCode(chr1);

            if (enc3 != 64) {
                output = output + String.fromCharCode(chr2);
            }
            if (enc4 != 64) {
                output = output + String.fromCharCode(chr3);
            }

        }
        output = _utf8_decode(output);
        return output;
    }

    // private method for UTF-8 encoding
    var _utf8_encode = function (string) {
        string = string.replace(/\r\n/g, "\n");
        var utftext = "";

        for (var n = 0; n < string.length; n++) {

            var c = string.charCodeAt(n);

            if (c < 128) {
                utftext += String.fromCharCode(c);
            } else if ((c > 127) && (c < 2048)) {
                utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            } else {
                utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }

        }

        return utftext;
    }

    // private method for UTF-8 decoding
    var _utf8_decode = function (utftext) {
        var string = "";
        var i = 0;
        var c = 0, c3 = 0, c2 = 0;

        while (i < utftext.length) {

            c = utftext.charCodeAt(i);

            if (c < 128) {
                string += String.fromCharCode(c);
                i++;
            } else if ((c > 191) && (c < 224)) {
                c2 = utftext.charCodeAt(i + 1);
                string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                i += 2;
            } else {
                c2 = utftext.charCodeAt(i + 1);
                c3 = utftext.charCodeAt(i + 2);
                string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                i += 3;
            }

        }
        return string;
    }
}

//js写的Hashtable类
function Hashtable() {
    this._hash = new Object();
    this.add = function (key, value) {
        if (typeof (key) != "undefined") {
            if (this.contains(key) == false) {
                this._hash[key] = typeof (value) == "undefined" ? null : value;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    this.remove = function (key) { delete this._hash[key]; }
    this.remove_value = function (value) {
        if (value != null) {
            for (var key in this._hash) {
                if (this._hash[key] == value) {
                    delete this._hash[key];
                    break;
                }
            }
        }
    }
    this.count = function () { var i = 0; for (var k in this._hash) { i++; } return i; }
    this.items = function (key) { return this._hash[key]; }
    this.contains = function (key) { return typeof (this._hash[key]) != "undefined"; }
    this.clear = function () { for (var k in this._hash) { delete this._hash[k]; } }
}

