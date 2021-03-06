/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	m3-account-web-main
 * 文件名：	CPInvoiceReceive.java
 * 模块说明：	
 * 修改历史：
 * 2015年12月13日 - lixiaohong - 创建。
 */
package com.hd123.m3.account.gwt.ivc.receive.intf.client.dd;

import com.hd123.m3.account.gwt.ivc.intf.client.dd.CPInvoiceStandardBill;

/**
 * @author lixiaohong
 *
 */
public interface CPInvoiceReceive extends CPInvoiceStandardBill {
  public String tableCaption();
  public String issuer();
  public String receiver();
  public String receiveDate();
}
