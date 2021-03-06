/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2013，所有权利保留。
 * 
 * 项目名： account-web-lease
 * 文件名： CashDefrayalGadget.java
 * 模块说明：    
 * 修改历史：
 * 2013-10-28 - subinzhu - 创建。
 */
package com.hd123.m3.account.gwt.payment.pay.client.ui.gadget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.hd123.m3.account.gwt.payment.commons.client.biz.BPaymentCashDefrayal;
import com.hd123.m3.account.gwt.payment.commons.client.gadget.PaymentHasFocusables;
import com.hd123.m3.account.gwt.payment.commons.intf.client.dd.PPaymentCashDefrayalDef;
import com.hd123.m3.account.gwt.payment.pay.client.EPPayment;
import com.hd123.m3.account.gwt.payment.pay.client.PaymentMessages;
import com.hd123.m3.account.gwt.payment.rec.client.biz.ActionName;
import com.hd123.rumba.commons.gwt.mini.client.ObjectUtil;
import com.hd123.rumba.commons.gwt.mini.client.StringUtil;
import com.hd123.rumba.gwt.base.client.BUCN;
import com.hd123.rumba.gwt.util.client.message.Message;
import com.hd123.rumba.gwt.widget2.client.panel.RCaptionBox;
import com.hd123.rumba.gwt.widget2.client.panel.RVerticalPanel;
import com.hd123.rumba.gwt.widget2e.client.event.HasRActionHandlers;
import com.hd123.rumba.gwt.widget2e.client.event.RActionEvent;
import com.hd123.rumba.gwt.widget2e.client.event.RActionHandler;

/**
 * 按总额收款，实收明细编辑控件
 * 
 * @author subinzhu
 * 
 */
public class CashDefrayalEditGadget extends RCaptionBox implements RActionHandler,
    HasRActionHandlers, PaymentHasFocusables, Focusable {

  private List<BPaymentCashDefrayal> values;
  private FlexTable tableTitle = new FlexTable();
  private RVerticalPanel detailTable;
  private List<CashDefrayalLineEditGadget> detailGadgets = new ArrayList<CashDefrayalLineEditGadget>();

  public CashDefrayalEditGadget() {
    super();
    drawSelf();
  }

  private void drawSelf() {
    RVerticalPanel vp = new RVerticalPanel();

    vp.add(drawDetailsTitle());
    vp.add(initDetail());

    setCaption(PaymentMessages.M.defrayal());
    setStyleName(RCaptionBox.STYLENAME_CONCISE);
    setWidth("100%");
    setContent(vp);
  }

  private Widget drawDetailsTitle() {
    if (tableTitle == null)
      tableTitle = new FlexTable();

    tableTitle.clear();

    tableTitle.setWidget(0, 0, new HTML(PaymentMessages.M.paymentType()));
    tableTitle.getColumnFormatter().setWidth(0, CashDefrayalLineEditGadget.WIDTH_DETAIL_COL0);

    tableTitle.setWidget(0, 1, new HTML(PPaymentCashDefrayalDef.constants.total()));
    tableTitle.getColumnFormatter().setWidth(1, CashDefrayalLineEditGadget.WIDTH_DETAIL_COL1);

    tableTitle.setWidget(0, 2, new HTML(PPaymentCashDefrayalDef.constants.bankCode()));
    tableTitle.getColumnFormatter().setWidth(2, CashDefrayalLineEditGadget.WIDTH_DETAIL_COL2);

    tableTitle.setWidget(0, 3, new HTML(PPaymentCashDefrayalDef.constants.remark()));
    tableTitle.getColumnFormatter().setWidth(3, CashDefrayalLineEditGadget.WIDTH_DETAIL_COL3);
    return tableTitle;
  }

  private Widget initDetail() {
    if (detailTable == null) {
      detailTable = new RVerticalPanel();
    }

    detailTable.clear();
    detailGadgets.clear();

    return detailTable;
  }

  @Override
  public void clearValidResults() {
    for (CashDefrayalLineEditGadget g : detailGadgets)
      g.clearValidResults();
  }

  @Override
  public boolean isValid() {
    boolean isValid = true;
    for (CashDefrayalLineEditGadget g : detailGadgets)
      isValid &= g.isValid();
    return isValid;
  }

  @Override
  public List<Message> getInvalidMessages() {
    List<Message> messages = new ArrayList<Message>();
    for (CashDefrayalLineEditGadget g : detailGadgets)
      messages.addAll(g.getInvalidMessages());
    return messages;
  }

  @Override
  public boolean validate() {
    clearValidResults();
    boolean isValid = true;
    for (CashDefrayalLineEditGadget g : detailGadgets)
      isValid &= g.validate();
    return isValid;
  }

  public List<BPaymentCashDefrayal> getValue() {
    return values;
  }

  public void setValue(List<BPaymentCashDefrayal> list) {
    this.values = list;
    refreshLineNumber();
    refreshDetail();
  }

  private void refreshLineNumber() {
    for (int i = 0; i < values.size(); i++) {
      values.get(i).setLineNumber(i);
    }
  }

  private void refreshDetail() {
    initDetail();

    if (values == null)
      return;

    if (values.isEmpty()) {
      BPaymentCashDefrayal cashDefrayal = new BPaymentCashDefrayal();
      cashDefrayal.setPaymentType(EPPayment.getInstance().getDefaultOption().getPaymentType());
      values.add(cashDefrayal);
    }

    for (int i = 0; i < values.size(); i++) {
      BPaymentCashDefrayal cash = values.get(i);
      if (cash.getBank() != null && StringUtil.isNullOrBlank(cash.getBank().getCode())) {
        cash.setBank(null);
      }
      addDetail(cash, i);
    }
  }

  @Override
  public void onAction(RActionEvent event) {
    if (ObjectUtil.equals(event.getActionName(), ActionName.ACTION_CASHDEFRAYALLINE_ADDDETAIL)) {
      int index = (Integer) event.getParameters().iterator().next();
      BPaymentCashDefrayal newDetail = new BPaymentCashDefrayal();

      insertDetail(newDetail, index + 1);
      refreshLineNumber();
    }
    if (ObjectUtil.equals(event.getActionName(), ActionName.ACTION_CASHDEFRAYALLINE_REMOVEDETAIL)) {
      int index = (Integer) event.getParameters().iterator().next();
      removeDetail(index);
      refreshLineNumber();

      RActionEvent.fire(CashDefrayalEditGadget.this, ActionName.ACTION_CASHDEFRAYAL_CHANGE);
    }
    if (ObjectUtil.equals(event.getActionName(), ActionName.ACTION_CASHDEFRAYALLINE_TOTALCHANGE)) {
      RActionEvent.fire(CashDefrayalEditGadget.this, ActionName.ACTION_CASHDEFRAYAL_CHANGE);
    }
  }

  private void insertDetail(BPaymentCashDefrayal detail, int insertRow) {
    values.add(insertRow, detail);

    addDetail(detail, insertRow);
  }

  private void addDetail(BPaymentCashDefrayal detail, int insertRow) {
    CashDefrayalLineEditGadget gadget = new CashDefrayalLineEditGadget();
    gadget.setValue(detail);
    gadget.addActionHandler(this);
    detailGadgets.add(insertRow, gadget);
    detailTable.insert(gadget, insertRow);
  }

  private void removeDetail(int currentRow) {
    if (values.size() > 1) {
      values.remove(currentRow);
      detailGadgets.remove(currentRow);
      detailTable.remove(currentRow);
    } else {
      values.clear();
      detailGadgets.clear();
      detailTable.clear();

      BPaymentCashDefrayal newDetail = new BPaymentCashDefrayal();
      newDetail.setPaymentType(EPPayment.getInstance().getDefaultOption().getPaymentType());
      insertDetail(newDetail, 0);
    }
  }

  @Override
  public HandlerRegistration addActionHandler(RActionHandler handler) {
    return addHandler(handler, RActionEvent.getType());
  }

  @Override
  public Focusable getFocusable(int lineNumber, String field) {
    if (detailGadgets == null || detailGadgets.isEmpty() || lineNumber < 0
        || detailGadgets.size() <= lineNumber)
      return null;
    else
      return detailGadgets.get(lineNumber).getFocusable(field);
  }

  @Override
  public int getTabIndex() {
    Focusable field = getFocusableField();
    return field == null ? 0 : field.getTabIndex();
  }

  @Override
  public void setAccessKey(char key) {
    Focusable field = getFocusableField();
    if (field != null)
      field.setAccessKey(key);
  }

  @Override
  public void setFocus(boolean focus) {
    Focusable field = getFocusableField();
    if (field != null)
      field.setFocus(focus);
  }

  @Override
  public void setTabIndex(int index) {
    Focusable field = getFocusableField();
    if (field != null)
      field.setTabIndex(index);
  }

  public void setStore(BUCN store, boolean clearValue) {
    for (CashDefrayalLineEditGadget gadget : detailGadgets) {
      gadget.setStore(store, clearValue);
    }
  }

  private Focusable getFocusableField() {
    if (detailGadgets.isEmpty())
      return null;
    return detailGadgets.get(0);
  }
}
