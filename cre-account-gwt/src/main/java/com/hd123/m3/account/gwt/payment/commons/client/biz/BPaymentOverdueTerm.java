/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2013，所有权利保留。
 * 
 * 项目名：	account-web-lease
 * 文件名：	BOverdueTerm.java
 * 模块说明：	
 * 修改历史：
 * 2013-11-11 - subinzhu - 创建。
 */
package com.hd123.m3.account.gwt.payment.commons.client.biz;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.hd123.m3.commons.gwt.base.client.tax.BTaxCalculator;
import com.hd123.m3.commons.gwt.base.client.tax.BTaxRate;
import com.hd123.rumba.commons.gwt.entity.client.BEntity;
import com.hd123.rumba.gwt.base.client.BUCN;

/**
 * @author subinzhu
 * 
 */
public class BPaymentOverdueTerm extends BEntity {

  private static final long serialVersionUID = -7406925590531913770L;

  public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
  public static final int SCALE_MONEY = 2;

  private BUCN contract;
  /** 存放滞纳金的科目 */
  private BUCN subject;
  private int direction;
  private BigDecimal rate = BigDecimal.ZERO;
  private BTaxRate taxRate;
  private boolean invoice;

  /** 产生滞纳金的科目 */
  private boolean allSubjects = false;
  private List<BUCN> subjects = new ArrayList<BUCN>();

  public BUCN getContract() {
    return contract;
  }

  public void setContract(BUCN contract) {
    this.contract = contract;
  }

  public BUCN getSubject() {
    return subject;
  }

  public void setSubject(BUCN subject) {
    this.subject = subject;
  }

  public int getDirection() {
    return direction;
  }

  public void setDirection(int direction) {
    this.direction = direction;
  }

  public BigDecimal getRate() {
    return rate;
  }

  public void setRate(BigDecimal rate) {
    this.rate = rate;
  }

  public BTaxRate getTaxRate() {
    return taxRate;
  }

  public void setTaxRate(BTaxRate taxRate) {
    this.taxRate = taxRate;
  }

  public boolean isInvoice() {
    return invoice;
  }

  public void setInvoice(boolean invoice) {
    this.invoice = invoice;
  }

  public boolean isAllSubjects() {
    return allSubjects;
  }

  public void setAllSubjects(boolean allSubjects) {
    this.allSubjects = allSubjects;
  }

  public List<BUCN> getSubjects() {
    return subjects;
  }

  public void setSubjects(List<BUCN> subjects) {
    this.subjects = subjects;
  }

  /**
   * 计算滞纳金金额
   * 
   * @param total
   *          滞纳金基数
   * @param overdueDays
   *          过期天数
   * @return 滞纳金金额
   */
  public BigDecimal calculateOverdueTotalTotal(BigDecimal total, long overdueDays) {
    assert total != null;
    assert rate != null;

    if (overdueDays <= 0)
      return BigDecimal.ZERO;

    return total.multiply(rate).multiply(new BigDecimal(overdueDays))
        .setScale(SCALE_MONEY, ROUNDING_MODE);
  }

  /**
   * 计算滞纳金税额
   * 
   * @param total
   *          滞纳金基数
   * @param scale
   * @param roundingMode
   * @return 滞纳金税额
   */
  public BigDecimal calculateOverdueTotalTax(BigDecimal total, int scale, RoundingMode roundingMode) {
    assert total != null;
    assert taxRate != null;

    if (taxRate.getRate() == null)
      return BigDecimal.ZERO;

    return BTaxCalculator.tax(total, taxRate, scale, roundingMode);
  }
}
