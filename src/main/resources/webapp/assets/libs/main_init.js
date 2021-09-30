'use strict'

//全局参数
function GetDateNow(format) {
	/***************************************************************************
	 * format=1表示获取年月日 format=2表示获取年月日时分秒
	 **************************************************************************/
	let _time = "";

	let now = new Date();
	let year = now.getFullYear();
	let month = now.getMonth() + 1;
	month = month < 10 ? ('0' + month) : month;
	let date = now.getDate();
	date = date < 10 ? ('0' + date) : date;

	let day = now.getDay();// 得到周几

	let hour = now.getHours();// 得到小时
	hour = hour < 10 ? ('0' + hour) : hour;
	let minu = now.getMinutes();// 得到分钟
	minu = minu < 10 ? ('0' + minu) : minu;
	let sec = now.getSeconds();// 得到秒
	sec = sec < 10 ? ('0' + sec) : sec;


	if (format == 1) {
		_time = year + "-" + month + "-" + date
	} else if (format == 2) {
		_time = year + "-" + month + "-" + date + " " + hour + ":" + minu + ":"
				+ sec
	}
	return _time
}

// 计算当前日期倒退几天时间或以后几天
function GetDateFormat(value) {
	let date1 = new Date();

	let month = date1.getMonth() + 1;
	month = month < 10 ? ('0' + month) : month;
	let date = date1.getDate();
	date = date < 10 ? ('0' + date) : date;

	let time1 = date1.getFullYear() + "-" + month + "-" + date;// time1表示当前时间

	let date2 = new Date(date1);
	date2.setDate(date1.getDate() + value);

	month = date2.getMonth() + 1;
	month = month < 10 ? ('0' + month) : month;
	date = date2.getDate();
	date = date < 10 ? ('0' + date) : date;

	let time2 = date2.getFullYear() + "-" + month + "-" + date;

	return time2;
}

// 提示信息
function elAlter(VM, vData, nType) {
	if (nType == 1) {
		VM.$notify.error({
			title : '错误',
			message : vData,
			duration : 1000 * 3
		});
	} else {
		VM.$notify({
			title : '成功',
			message : vData,
			type : 'success'
		});
	}

}