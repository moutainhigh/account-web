/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	m3-account-web-main
 * 文件名：	CPInvoiceExchange.java
 * 模块说明：	
 * 修改历史：
 * 2015年12月17日 - LiBin - 创建。
 */
package com.hd123.m3.account.gwt.ivc.exchange.intf.client.dd;

import com.hd123.m3.account.gwt.ivc.intf.client.dd.CPInvoiceStandardBill;

/**
 * 数据字典常量定义
 * 
 * @author LiBin
 * @since 1.7
 *
 */
public interface CPInvoiceExchange extends CPInvoiceStandardBill{

  public String tableCaption();
  
  public String exchangeType();
  
  public String invToInvType();
  
  public String exchanger();
  
  public String exchangeDate();
  
  public String oldInvoiceCode();
  
  public String oldInvoiceNumber();
  
}
