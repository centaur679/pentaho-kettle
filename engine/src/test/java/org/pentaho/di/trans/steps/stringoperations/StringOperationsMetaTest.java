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


package org.pentaho.di.trans.steps.stringoperations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;
import org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.StringLoadSaveValidator;


/**
 * User: Dzmitry Stsiapanau Date: 2/3/14 Time: 5:41 PM
 */
public class StringOperationsMetaTest implements InitializerInterface<StepMetaInterface> {
  LoadSaveTester loadSaveTester;
  Class<StringOperationsMeta> testMetaClass = StringOperationsMeta.class;
  @ClassRule public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

  @Before
  public void setUpLoadSave() throws Exception {
    KettleEnvironment.init();
    PluginRegistry.init( false );
    List<String> attributes =
        Arrays.asList( "padLen", "padChar", "fieldInStream", "fieldOutStream", "trimType", "lowerUpper", "initCap", "maskXML", "digits", "removeSpecialCharacters", "paddingType" );

    FieldLoadSaveValidator<String[]> stringArrayLoadSaveValidator =
        new ArrayLoadSaveValidator<String>( new StringLoadSaveValidator(), 5 );

    Map<String, FieldLoadSaveValidator<?>> attrValidatorMap = new HashMap<String, FieldLoadSaveValidator<?>>();
    attrValidatorMap.put( "padLen", stringArrayLoadSaveValidator );
    attrValidatorMap.put( "padChar", stringArrayLoadSaveValidator );
    attrValidatorMap.put( "fieldInStream", stringArrayLoadSaveValidator );
    attrValidatorMap.put( "fieldOutStream", stringArrayLoadSaveValidator );
    attrValidatorMap.put( "trimType",
      new ArrayLoadSaveValidator<String>( new StringLoadSaveValidator(), 5 ) );
    attrValidatorMap.put( "lowerUpper",
      new ArrayLoadSaveValidator<String>( new StringLoadSaveValidator(), 5 ) );
    attrValidatorMap.put( "initCap",
      new ArrayLoadSaveValidator<String>( new StringLoadSaveValidator(), 5 ) );
    attrValidatorMap.put( "maskXML",
      new ArrayLoadSaveValidator<String>( new StringLoadSaveValidator(), 5 ) );
    attrValidatorMap.put( "digits",
      new ArrayLoadSaveValidator<String>( new StringLoadSaveValidator(), 5 ) );
    attrValidatorMap.put( "removeSpecialCharacters",
      new ArrayLoadSaveValidator<String>( new StringLoadSaveValidator(), 5 ) );
    attrValidatorMap.put( "paddingType",
      new ArrayLoadSaveValidator<String>( new StringLoadSaveValidator(), 5 ) );

    Map<String, FieldLoadSaveValidator<?>> typeValidatorMap = new HashMap<String, FieldLoadSaveValidator<?>>();

    loadSaveTester =
        new LoadSaveTester( testMetaClass, attributes, new ArrayList<String>(), new ArrayList<String>(),
            new HashMap<String, String>(), new HashMap<String, String>(), attrValidatorMap, typeValidatorMap, this );
  }

  // Call the allocate method on the LoadSaveTester meta class
  @Override
  public void modify( StepMetaInterface someMeta ) {
    if ( someMeta instanceof StringOperationsMeta ) {
      ( (StringOperationsMeta) someMeta ).allocate( 5 );
    }
  }

  @Test
  public void testSerialization() throws KettleException {
    loadSaveTester.testSerialization();
  }

  @Test
  public void testGetFields() throws Exception {
    StringOperationsMeta meta = new StringOperationsMeta();
    meta.allocate( 1 );
    meta.setFieldInStream( new String[] { "field1" } );

    RowMetaInterface rowMetaInterface = new RowMeta();
    ValueMetaInterface valueMeta = new ValueMetaString( "field1" );
    valueMeta.setStorageMetadata( new ValueMetaString( "field1" ) );
    valueMeta.setStorageType( ValueMetaInterface.STORAGE_TYPE_BINARY_STRING );
    rowMetaInterface.addValueMeta( valueMeta );

    VariableSpace space = mock( VariableSpace.class );
    meta.getFields( null, rowMetaInterface, "STRING_OPERATIONS", null, null, space, null, null );
    RowMetaInterface expectedRowMeta = new RowMeta();
    expectedRowMeta.addValueMeta( new ValueMetaString( "field1" ) );
    assertEquals( expectedRowMeta.toString(), rowMetaInterface.toString() );
  }
}
