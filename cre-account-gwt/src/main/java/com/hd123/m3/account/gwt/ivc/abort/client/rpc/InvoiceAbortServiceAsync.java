/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	m3-account-web-main
 * 文件名：	InvoiceAbortServiceAsync.java
 * 模块说明：	
 * 修改历史：
 * 2015年12月15日 - lixiaohong - 创建。
 */
package com.hd123.m3.account.gwt.ivc.abort.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hd123.m3.account.gwt.ivc.abort.client.biz.BInvoiceAbort;
import com.hd123.m3.commons.gwt.bpm.client.rpc2.M3BpmModuleService2Async;
import com.hd123.rumba.gwt.base.client.BUCN;

/**
 * 发票作废单|异步接口
 * 
 * @author lixiaohong
 * @since 1.7
 * 
 */
public interface InvoiceAbortServiceAsync extends M3BpmModuleService2Async<BInvoiceAbort> {
  public void getCurrentEmployee(String id, AsyncCallback<BUCN> callback);
}
