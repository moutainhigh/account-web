/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2013，所有权利保留。
 * 
 * 项目名： account-web-lease
 * 文件名： PayLineViewGadget.java
 * 模块说明：    
 * 修改历史：
 * 2013-10-14 - chenpeisi - 创建。
 */
package com.hd123.m3.account.gwt.deposit.receipt.client.ui.gadget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.hd123.m3.account.gwt.deposit.commons.client.biz.BDeposit;
import com.hd123.m3.account.gwt.deposit.commons.client.biz.BDepositLine;
import com.hd123.m3.account.gwt.deposit.intf.client.DepositMessage;
import com.hd123.m3.account.gwt.deposit.intf.client.dd.PDepositLineDef;
import com.hd123.m3.account.gwt.subject.intf.client.SubjectUrlParams;
import com.hd123.m3.account.gwt.subject.intf.client.dd.PSubjectDef;
import com.hd123.m3.commons.gwt.base.client.biz.BBizStates;
import com.hd123.m3.commons.gwt.base.client.format.M3Format;
import com.hd123.rumba.commons.gwt.mini.client.StringUtil;
import com.hd123.rumba.gwt.util.client.GWTUtil;
import com.hd123.rumba.gwt.widget2.client.dialog.RMsgBox;
import com.hd123.rumba.gwt.widget2.client.event.LoadDataEvent;
import com.hd123.rumba.gwt.widget2.client.event.LoadDataHandler;
import com.hd123.rumba.gwt.widget2.client.grid.RGrid;
import com.hd123.rumba.gwt.widget2.client.grid.RGridCellInfo;
import com.hd123.rumba.gwt.widget2.client.grid.RGridColumnDef;
import com.hd123.rumba.gwt.widget2.client.grid.RGridDataProvider;
import com.hd123.rumba.gwt.widget2.client.grid.renderer.RHotItemRendererFactory;
import com.hd123.rumba.gwt.widget2.client.grid.renderer.RHyperlinkFieldRendererFactory;
import com.hd123.rumba.gwt.widget2.client.grid.renderer.RNumberRendererFactory;
import com.hd123.rumba.gwt.widget2.client.menu.RPopupMenu;
import com.hd123.rumba.gwt.widget2.client.panel.RCaptionBox;
import com.hd123.rumba.gwt.widget2.client.panel.RSimplePanel;
import com.hd123.rumba.gwt.widget2.client.panel.RVerticalPanel;
import com.hd123.rumba.webframe.gwt.base.client.RWindow;
import com.hd123.rumba.webframe.gwt.base.client.http.GwtUrl;
import com.hd123.rumba.webframe.gwt.base.client.i18n.GWTFormat;
import com.hd123.rumba.webframe.gwt.entrypoint.client.jump.JumpParameters;

/**
 * @author chenpeisi
 * 
 */
public class RecDepositLineViewGadget extends RCaptionBox {

  public RecDepositLineViewGadget() {
    drawSelf();
  }

  private BDeposit entity;

  private Label captionCount;
  private RGrid grid;
  private RGridColumnDef lineNumberCol;
  private RGridColumnDef subjectCol;
  private RGridColumnDef totalCol;
  private RGridColumnDef remainTotalCol;
  private RGridColumnDef contractTotalCol;
  private RGridColumnDef remarkCol;

  private RPopupMenu lineMenu;

  private void drawSelf() {
    RVerticalPanel vp = new RVerticalPanel();
    vp.setWidth("100%");

    vp.add(drawGrid());

    captionCount = new Label(DepositMessage.M.resultTotal(0));
    setCaption(DepositMessage.M.recDepositInfo());
    setWidth("100%");
    setContent(vp);

    getCaptionBar().addButton(RSimplePanel.decoratePadding(captionCount, 0, 10, 0, 10));
  }

  private Widget drawGrid() {
    grid = new RGrid();
    grid.setWidth("100%");
    grid.setShowRowSelector(false);
    grid.setHoverRow(true);
    grid.setProvider(new GridDataProvider());
    grid.addLoadDataHandler(new LoadDataHandler() {

      @Override
      public void onLoadData(LoadDataEvent event) {
        if (entity == null || entity.getLines() == null)
          return;
        captionCount.setText(DepositMessage.M.resultTotal(entity.getLines().size()));
      }
    });
    grid.addClickHandler(new Handler_grid());

    lineNumberCol = new RGridColumnDef(PDepositLineDef.lineNumber);
    lineNumberCol.setWidth("50px");
    grid.addColumnDef(lineNumberCol);

    subjectCol = new RGridColumnDef(PDepositLineDef.subject);
    subjectCol.setRendererFactory(new RHotItemRendererFactory(lineMenu,
        new RHyperlinkFieldRendererFactory()));
    subjectCol.setWidth("180px");
    grid.addColumnDef(subjectCol);

    totalCol = new RGridColumnDef(PDepositLineDef.amount);
    totalCol.setRendererFactory(new RNumberRendererFactory(GWTFormat.fmt_money));
    totalCol.setHorizontalAlign(HasHorizontalAlignment.ALIGN_RIGHT);
    totalCol.setWidth("100px");
    grid.addColumnDef(totalCol);

    remainTotalCol = new RGridColumnDef(PDepositLineDef.remainTotal);
    remainTotalCol.setRendererFactory(new RNumberRendererFactory(GWTFormat.fmt_money));
    remainTotalCol.setHorizontalAlign(HasHorizontalAlignment.ALIGN_RIGHT);
    remainTotalCol.setWidth("100px");
    grid.addColumnDef(remainTotalCol);

    contractTotalCol = new RGridColumnDef(DepositMessage.M.contractTotal());
    contractTotalCol.setHorizontalAlign(HasHorizontalAlignment.ALIGN_RIGHT);
    contractTotalCol.setRendererFactory(new RNumberRendererFactory(M3Format.fmt_money));
    contractTotalCol.setWidth("100px");
    grid.addColumnDef(contractTotalCol);

    remarkCol = new RGridColumnDef(PDepositLineDef.remark);
    grid.addColumnDef(remarkCol);

    grid.setAllColumnsOverflowEllipsis(true);
    return grid;
  }

  public void refresh(BDeposit entity) {
    assert entity != null;
    this.entity = entity;

    grid.refresh();

  }

  public void refreshCommands() {
    remainTotalCol.setVisible(BBizStates.INEFFECT.equals(entity.getBizState()));
    grid.rebuild();
  }

  private class Handler_grid implements ClickHandler {

    @Override
    public void onClick(ClickEvent event) {
      GWTUtil.blurActiveElement();
      RGridCellInfo cell = grid.getCellForEvent(event.getNativeEvent());
      if (cell == null)
        return;

      if (cell.getColumnDef().equals(subjectCol)) {
        BDepositLine line = entity.getLines().get(grid.getCurrentRow());
        if (line == null)
          return;
        if (line.getSubject() == null || StringUtil.isNullOrBlank(line.getSubject().getUuid()))
          return;
        GwtUrl url = SubjectUrlParams.ENTRY_URL;
        url.getQuery().set(JumpParameters.PN_START, SubjectUrlParams.View.START_NODE);
        url.getQuery().set(SubjectUrlParams.View.PN_ENTITY_UUID, line.getSubject().getUuid());
        try {
          RWindow.navigate(url.getUrl(), RWindow.WINDOWNAME_NEW);
        } catch (Exception e) {
          String msg = DepositMessage.M.cannotNavigate(PSubjectDef.TABLE_CAPTION);
          RMsgBox.showError(msg, e);
        }
      }
    }
  }

  private class GridDataProvider implements RGridDataProvider {

    @Override
    public int getRowCount() {
      if (entity == null || entity.getLines() == null) {
        return 0;
      }
      return entity.getLines().size();
    }

    @Override
    public Object getData(int row, int col) {
      if (entity.getLines() == null || entity.getLines().isEmpty())
        return null;
      if (col == lineNumberCol.getIndex()) {
        return entity.getLines().get(row).getLineNumber();
      } else if (col == subjectCol.getIndex()) {
        return entity.getLines().get(row).getSubject() == null ? null : entity.getLines().get(row)
            .getSubject().toFriendlyStr();
      } else if (col == totalCol.getIndex()) {
        return entity.getLines().get(row).getTotal() == null ? null : entity.getLines().get(row)
            .getTotal().doubleValue();
      } else if (col == remainTotalCol.getIndex()) {
        return entity.getLines().get(row).getRemainTotal() == null ? null : entity.getLines()
            .get(row).getRemainTotal().doubleValue();
      } else if (col == contractTotalCol.getIndex()) {
        return entity.getLines().get(row).getContractTotal() == null ? null : entity.getLines()
            .get(row).getContractTotal().doubleValue();
      } else if (col == remarkCol.getIndex()) {
        return entity.getLines().get(row).getRemark();
      }
      return null;
    }
  }
}
