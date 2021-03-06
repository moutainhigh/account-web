/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	account-ls-web-lease
 * 文件名：	FreezeLogPage.java
 * 模块说明：	
 * 修改历史：
 * 2014-3-3 - zhuhairui - 创建。
 */
package com.hd123.m3.account.gwt.freeze.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.hd123.ia.widget.audit.client.AuditQueryParams;
import com.hd123.ia.widget.audit.client.BKeyword;
import com.hd123.m3.account.gwt.base.client.LoggerConstant;
import com.hd123.m3.account.gwt.freeze.client.EPFreeze;
import com.hd123.m3.account.gwt.freeze.client.biz.BFreeze;
import com.hd123.m3.account.gwt.freeze.client.rpc.FreezeLoader;
import com.hd123.m3.account.gwt.freeze.intf.client.FreezeMessages;
import com.hd123.m3.account.gwt.freeze.intf.client.FreezeUrlParams.Log;
import com.hd123.m3.account.gwt.freeze.intf.client.dd.PFreezeDef;
import com.hd123.m3.account.gwt.freeze.intf.client.perm.FreezePermDef;
import com.hd123.m3.commons.gwt.base.client.log.M3AuditEventGrid;
import com.hd123.rumba.gwt.base.client.exception.ClientBizException;
import com.hd123.rumba.gwt.flecs.client.DefaultOperator;
import com.hd123.rumba.gwt.flecs.client.FlecsQueryDef.Condition;
import com.hd123.rumba.gwt.util.client.history.RHistory;
import com.hd123.rumba.gwt.widget2.client.action.RAction;
import com.hd123.rumba.gwt.widget2.client.action.RActionFacade;
import com.hd123.rumba.gwt.widget2.client.panel.RCardPanel;
import com.hd123.rumba.gwt.widget2.client.toolbar.RToolbarButton;
import com.hd123.rumba.gwt.widget2e.client.util.RTextStyles;
import com.hd123.rumba.webframe.gwt.entrypoint.client.BaseContentPage;
import com.hd123.rumba.webframe.gwt.entrypoint.client.jump.JumpParameters;
import com.hd123.rumba.webframe.gwt.noauthorized.intf.client.NoAuthorized;

/**
 * @author zhuhairui
 * 
 */
public class FreezeLogPage extends BaseContentPage implements Log {

  public static FreezeLogPage getInstance() throws ClientBizException {
    if (instance == null)
      instance = new FreezeLogPage();
    return instance;
  }

  public FreezeLogPage() throws ClientBizException {
    super();
    try {
      loader = new FreezeLoader();
      drawToolbar();
      drawSelf();
    } catch (Exception e) {
      throw new ClientBizException(FreezeMessages.M.cannotCreatePage("FreezeLogPage"), e);
    }
  }

  @Override
  public String getStartNode() {
    return START_NODE;
  }

  @Override
  public void onHide() {
  }

  @Override
  public void onShow(JumpParameters params) {
    super.onShow(params);

    if (!checkIn())
      return;

    loader.decoderParams(params, new Command() {
      @Override
      public void execute() {
        entity = loader.getEntity();

        refreshTitle();
        refreshEntity();
      }
    });
  }

  private EPFreeze ep = EPFreeze.getInstance();
  private BFreeze entity;
  private FreezeLoader loader;
  private static FreezeLogPage instance;

  private Label cardPanellabel;

  private RAction backAction;
  private M3AuditEventGrid logGadget;

  private void drawToolbar() {
    backAction = new RAction(RActionFacade.BACK_TO_VIEW, new Handler_backAction());
    getToolbar().add(new RToolbarButton(backAction));
  }

  private void drawSelf() {
    VerticalPanel panel = new VerticalPanel();
    panel.setWidth("100%");
    panel.setSpacing(8);
    initWidget(panel);

    Widget w = drawCardPanel();
    panel.add(w);

    logGadget = new M3AuditEventGrid();
    panel.add(logGadget);
  }

  private Widget drawCardPanel() {
    cardPanellabel = new Label("");
    cardPanellabel.setStyleName(RTextStyles.STYLE_OVERVIEW_TITLE);

    RCardPanel cardPanel = new RCardPanel(cardPanellabel);
    cardPanel.setContentSpacing(10);
    cardPanel.setWidth("100%");
    return cardPanel;
  }

  private boolean checkIn() {
    // if (fromBPM)
    // return true;
    if (ep.isPermitted(FreezePermDef.CREATE) == false) {
      NoAuthorized.open(ep.getModuleCaption());
      return false;
    }
    return true;
  }

  private void refreshTitle() {
    ep.getTitleBar().clearStandardTitle();
    ep.getTitleBar().setTitleText(ep.getModuleCaption());
    if (entity != null) {
      ep.getTitleBar().appendAttributeText(entity.getBillNumber());
      ep.getTitleBar().appendAttributeText(FreezeMessages.M.log(), "-");
    }
  }

  private void refreshEntity() {
    cardPanellabel.setText(entity.getBillNumber() + ","
        + PFreezeDef.state.getEnumCaption(entity.getState()));

    logGadget.setParams(getConditions());
    logGadget.getPagingGrid().refresh();
  }

  private List<Condition> getConditions() {
    List<Condition> conditions = new ArrayList<Condition>();
    Condition condition = new Condition();
    condition.setFieldName(AuditQueryParams.FIELD_KEY);
    condition.setOperator(DefaultOperator.EQUALS);
    ArrayList<BKeyword> keywords = new ArrayList<BKeyword>();

    if (entity != null) {
      BKeyword keyword = new BKeyword(LoggerConstant.INDEX_ZORE, LoggerConstant.KEY_UUID,
          entity.getUuid());
      keywords.add(keyword);
    }

    condition.setOperand(keywords);
    conditions.add(condition);

    return conditions;
  }

  private class Handler_backAction implements ClickHandler {

    @Override
    public void onClick(ClickEvent event) {
      RHistory.back();
    }
  }

}
