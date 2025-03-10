/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.di.ui.trans.steps.writetolog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.writetolog.WriteToLogMeta;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.StyledTextComp;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.di.ui.trans.step.TableItemInsertListener;

public class WriteToLogDialog extends BaseStepDialog implements StepDialogInterface {
  private static Class<?> PKG = WriteToLogMeta.class; // for i18n purposes, needed by Translator2!!

  private WriteToLogMeta input;
  private Label wlLoglevel;

  private CCombo wLoglevel;

  private Label wlPrintHeader;
  private FormData fdPrintHeader, fdlPrintHeader;

  private Button wPrintHeader;
  private FormData fdlLoglevel, fdLoglevel;

  private Label wlLogMessage;
  private StyledTextComp wLogMessage;
  private FormData fdlLogMessage, fdLogMessage;

  private Label wlFields;
  private TableView wFields;
  private FormData fdlFields, fdFields;

  private Label wlLimitRows;
  private Button wLimitRows;
  private FormData fdlLimitRows, fdLimitRows;

  private Label wlLimitRowsNumber;
  private Text wLimitRowsNumber;
  private FormData fdlLimitRowsNumber, fdLimitRowsNumber;

  private Map<String, Integer> inputFields;

  private ColumnInfo[] colinf;

  public WriteToLogDialog( Shell parent, Object in, TransMeta tr, String sname ) {
    super( parent, (BaseStepMeta) in, tr, sname );
    input = (WriteToLogMeta) in;
    inputFields = new HashMap<String, Integer>();
  }

  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX );
    props.setLook( shell );
    setShellImage( shell, input );

    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        input.setChanged();
      }
    };

    SelectionAdapter lsSelMod = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        input.setChanged();
      }
    };

    SelectionAdapter lsLimitRows = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        input.setChanged();
        enableFields();
      }
    };

    changed = input.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "WriteToLogDialog.Shell.Title" ) );

    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    // Stepname line
    wlStepname = new Label( shell, SWT.RIGHT );
    wlStepname.setText( BaseMessages.getString( PKG, "WriteToLogDialog.Stepname.Label" ) );
    props.setLook( wlStepname );
    fdlStepname = new FormData();
    fdlStepname.left = new FormAttachment( 0, 0 );
    fdlStepname.right = new FormAttachment( middle, -margin );
    fdlStepname.top = new FormAttachment( 0, margin );
    wlStepname.setLayoutData( fdlStepname );
    wStepname = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wStepname.setText( stepname );
    props.setLook( wStepname );
    wStepname.addModifyListener( lsMod );
    fdStepname = new FormData();
    fdStepname.left = new FormAttachment( middle, 0 );
    fdStepname.top = new FormAttachment( 0, margin );
    fdStepname.right = new FormAttachment( 100, 0 );
    wStepname.setLayoutData( fdStepname );

    // Log Level
    wlLoglevel = new Label( shell, SWT.RIGHT );
    wlLoglevel.setText( BaseMessages.getString( PKG, "WriteToLogDialog.Loglevel.Label" ) );
    props.setLook( wlLoglevel );
    fdlLoglevel = new FormData();
    fdlLoglevel.left = new FormAttachment( 0, 0 );
    fdlLoglevel.right = new FormAttachment( middle, -margin );
    fdlLoglevel.top = new FormAttachment( wStepname, margin );
    wlLoglevel.setLayoutData( fdlLoglevel );
    wLoglevel = new CCombo( shell, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
    wLoglevel.setItems( LogLevel.getLogLevelDescriptions() );
    props.setLook( wLoglevel );
    fdLoglevel = new FormData();
    fdLoglevel.left = new FormAttachment( middle, 0 );
    fdLoglevel.top = new FormAttachment( wStepname, margin );
    fdLoglevel.right = new FormAttachment( 100, 0 );
    wLoglevel.setLayoutData( fdLoglevel );
    wLoglevel.addSelectionListener( lsSelMod );

    // print header?
    wlPrintHeader = new Label( shell, SWT.RIGHT );
    wlPrintHeader.setText( BaseMessages.getString( PKG, "WriteToLogDialog.PrintHeader.Label" ) );
    props.setLook( wlPrintHeader );
    fdlPrintHeader = new FormData();
    fdlPrintHeader.left = new FormAttachment( 0, 0 );
    fdlPrintHeader.top = new FormAttachment( wLoglevel, margin );
    fdlPrintHeader.right = new FormAttachment( middle, -margin );
    wlPrintHeader.setLayoutData( fdlPrintHeader );
    wPrintHeader = new Button( shell, SWT.CHECK );
    wPrintHeader.setToolTipText( BaseMessages.getString( PKG, "WriteToLogDialog.PrintHeader.Tooltip" ) );
    props.setLook( wPrintHeader );
    fdPrintHeader = new FormData();
    fdPrintHeader.left = new FormAttachment( middle, 0 );
    fdPrintHeader.top = new FormAttachment( wLoglevel, margin );
    fdPrintHeader.right = new FormAttachment( 100, 0 );
    wPrintHeader.setLayoutData( fdPrintHeader );
    wPrintHeader.addSelectionListener( lsSelMod );

    // Limit output?
    // Cache?
    wlLimitRows = new Label( shell, SWT.RIGHT );
    wlLimitRows.setText( BaseMessages.getString( PKG, "DatabaseLookupDialog.LimitRows.Label" ) );
    props.setLook( wlLimitRows );
    fdlLimitRows = new FormData();
    fdlLimitRows.left = new FormAttachment( 0, 0 );
    fdlLimitRows.right = new FormAttachment( middle, -margin );
    fdlLimitRows.top = new FormAttachment( wPrintHeader, margin );
    wlLimitRows.setLayoutData( fdlLimitRows );
    wLimitRows = new Button( shell, SWT.CHECK );
    props.setLook( wLimitRows );
    fdLimitRows = new FormData();
    fdLimitRows.left = new FormAttachment( middle, 0 );
    fdLimitRows.top = new FormAttachment( wPrintHeader, margin );
    wLimitRows.setLayoutData( fdLimitRows );
    wLimitRows.addSelectionListener( lsLimitRows );

    // LimitRows size line
    wlLimitRowsNumber = new Label( shell, SWT.RIGHT );
    wlLimitRowsNumber.setText( BaseMessages.getString( PKG, "DatabaseLookupDialog.LimitRowsNumber.Label" ) );
    props.setLook( wlLimitRowsNumber );
    wlLimitRowsNumber.setEnabled( input.isLimitRows() );
    fdlLimitRowsNumber = new FormData();
    fdlLimitRowsNumber.left = new FormAttachment( 0, 0 );
    fdlLimitRowsNumber.right = new FormAttachment( middle, -margin );
    fdlLimitRowsNumber.top = new FormAttachment( wLimitRows, margin );
    wlLimitRowsNumber.setLayoutData( fdlLimitRowsNumber );
    wLimitRowsNumber = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wLimitRowsNumber );
    wLimitRowsNumber.setEnabled( input.isLimitRows() );
    wLimitRowsNumber.addModifyListener( lsMod );
    fdLimitRowsNumber = new FormData();
    fdLimitRowsNumber.left = new FormAttachment( middle, 0 );
    fdLimitRowsNumber.right = new FormAttachment( 100, 0 );
    fdLimitRowsNumber.top = new FormAttachment( wLimitRows, margin );
    wLimitRowsNumber.setLayoutData( fdLimitRowsNumber );

    // Log message to display
    wlLogMessage = new Label( shell, SWT.RIGHT );
    wlLogMessage.setText( BaseMessages.getString( PKG, "WriteToLogDialog.Shell.Title" ) );
    props.setLook( wlLogMessage );
    fdlLogMessage = new FormData();
    fdlLogMessage.left = new FormAttachment( 0, 0 );
    fdlLogMessage.top = new FormAttachment( wLimitRowsNumber, margin );
    fdlLogMessage.right = new FormAttachment( middle, -margin );
    wlLogMessage.setLayoutData( fdlLogMessage );

    wLogMessage =
      new StyledTextComp( transMeta, shell, SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL, "" );
    props.setLook( wLogMessage, Props.WIDGET_STYLE_FIXED );
    wLogMessage.addModifyListener( lsMod );
    fdLogMessage = new FormData();
    fdLogMessage.left = new FormAttachment( middle, 0 );
    fdLogMessage.top = new FormAttachment( wLimitRowsNumber, margin );
    fdLogMessage.right = new FormAttachment( 100, -2 * margin );
    fdLogMessage.height = 125;
    wLogMessage.setLayoutData( fdLogMessage );

    wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wGet = new Button( shell, SWT.PUSH );
    wGet.setText( BaseMessages.getString( PKG, "System.Button.GetFields" ) );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

    setButtonPositions( new Button[] { wOK, wGet, wCancel }, margin, null );

    // Table with fields
    wlFields = new Label( shell, SWT.NONE );
    wlFields.setText( BaseMessages.getString( PKG, "WriteToLogDialog.Fields.Label" ) );
    props.setLook( wlFields );
    fdlFields = new FormData();
    fdlFields.left = new FormAttachment( 0, 0 );
    fdlFields.top = new FormAttachment( wLogMessage, margin );
    wlFields.setLayoutData( fdlFields );

    final int FieldsCols = 1;
    final int FieldsRows = input.getFieldName().length;

    colinf = new ColumnInfo[FieldsCols];
    colinf[0] =
      new ColumnInfo(
        BaseMessages.getString( PKG, "WriteToLogDialog.Fieldname.Column" ), ColumnInfo.COLUMN_TYPE_CCOMBO,
        new String[] { "" }, false );
    wFields =
      new TableView(
        transMeta, shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, colinf, FieldsRows, lsMod, props );

    fdFields = new FormData();
    fdFields.left = new FormAttachment( 0, 0 );
    fdFields.top = new FormAttachment( wlFields, margin );
    fdFields.right = new FormAttachment( 100, 0 );
    fdFields.bottom = new FormAttachment( wOK, -2 * margin );
    wFields.setLayoutData( fdFields );

    //
    // Search the fields in the background

    final Runnable runnable = new Runnable() {
      public void run() {
        StepMeta stepMeta = transMeta.findStep( stepname );
        if ( stepMeta != null ) {
          try {
            RowMetaInterface row = transMeta.getPrevStepFields( stepMeta );

            // Remember these fields...
            for ( int i = 0; i < row.size(); i++ ) {
              inputFields.put( row.getValueMeta( i ).getName(), Integer.valueOf( i ) );
            }
            setComboBoxes();
          } catch ( KettleException e ) {
            logError( BaseMessages.getString( PKG, "System.Dialog.GetFieldsFailed.Message" ) );
          }
        }
      }
    };
    new Thread( runnable ).start();

    // Add listeners
    lsCancel = new Listener() {
      public void handleEvent( Event e ) {
        cancel();
      }
    };
    lsGet = new Listener() {
      public void handleEvent( Event e ) {
        get();
      }
    };
    lsOK = new Listener() {
      public void handleEvent( Event e ) {
        ok();
      }
    };

    wCancel.addListener( SWT.Selection, lsCancel );
    wOK.addListener( SWT.Selection, lsOK );
    wGet.addListener( SWT.Selection, lsGet );

    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };

    wStepname.addSelectionListener( lsDef );

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    // Set the shell size, based upon previous time...
    setSize();

    getData();
    input.setChanged( changed );

    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return stepname;
  }

  protected void setComboBoxes() {
    // Something was changed in the row.
    //
    final Map<String, Integer> fields = new HashMap<String, Integer>();

    // Add the currentMeta fields...
    fields.putAll( inputFields );

    Set<String> keySet = fields.keySet();
    List<String> entries = new ArrayList<String>( keySet );

    String[] fieldNames = entries.toArray( new String[entries.size()] );

    Const.sortStrings( fieldNames );
    colinf[0].setComboValues( fieldNames );
  }

  private void get() {
    try {
      RowMetaInterface r = transMeta.getPrevStepFields( stepname );
      if ( r != null ) {
        TableItemInsertListener insertListener = new TableItemInsertListener() {
          public boolean tableItemInserted( TableItem tableItem, ValueMetaInterface v ) {
            return true;
          }
        };
        BaseStepDialog
          .getFieldsFromPrevious( r, wFields, 1, new int[] { 1 }, new int[] {}, -1, -1, insertListener );
      }
    } catch ( KettleException ke ) {
      new ErrorDialog( shell, BaseMessages.getString( PKG, "System.Dialog.GetFieldsFailed.Title" ), BaseMessages
        .getString( PKG, "System.Dialog.GetFieldsFailed.Message" ), ke );
    }

  }

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    wLoglevel.select( input.getLogLevelByDesc().getLevel() );

    wPrintHeader.setSelection( input.isdisplayHeader() );
    wLimitRows.setSelection( input.isLimitRows() );
    wLimitRowsNumber.setText( "" + input.getLimitRowsNumber() );

    if ( input.getLogMessage() != null ) {
      wLogMessage.setText( input.getLogMessage() );
    }

    Table table = wFields.table;
    if ( input.getFieldName().length > 0 ) {
      table.removeAll();
    }
    for ( int i = 0; i < input.getFieldName().length; i++ ) {
      TableItem ti = new TableItem( table, SWT.NONE );
      ti.setText( 0, "" + ( i + 1 ) );
      ti.setText( 1, input.getFieldName()[i] );
    }

    wFields.setRowNums();
    wFields.optWidth( true );

    wStepname.selectAll();
    wStepname.setFocus();
  }

  private void cancel() {
    stepname = null;
    input.setChanged( changed );
    dispose();
  }

  private void ok() {
    if ( Utils.isEmpty( wStepname.getText() ) ) {
      return;
    }
    stepname = wStepname.getText(); // return value

    input.setdisplayHeader( wPrintHeader.getSelection() );
    input.setLimitRows( wLimitRows.getSelection() );
    input.setLimitRowsNumber( Const.toInt( wLimitRowsNumber.getText(), 0 ) );

    if ( wLoglevel.getSelectionIndex() < 0 ) {
      input.setLogLevel( 3 ); // Basic
    } else {
      input.setLogLevel( wLoglevel.getSelectionIndex() );
    }

    if ( wLogMessage.getText() != null && wLogMessage.getText().length() > 0 ) {
      input.setLogMessage( wLogMessage.getText() );
    } else {
      input.setLogMessage( "" );
    }

    int nrfields = wFields.nrNonEmpty();
    input.allocate( nrfields );
    for ( int i = 0; i < nrfields; i++ ) {
      TableItem ti = wFields.getNonEmpty( i );
      //CHECKSTYLE:Indentation:OFF
      input.getFieldName()[i] = ti.getText( 1 );
    }
    dispose();
  }

  private void enableFields() {

    wLimitRowsNumber.setEnabled( wLimitRows.getSelection() );
    wlLimitRowsNumber.setEnabled( wLimitRows.getSelection() );

  }
}
