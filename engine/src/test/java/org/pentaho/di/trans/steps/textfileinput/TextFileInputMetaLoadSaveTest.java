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


package org.pentaho.di.trans.steps.textfileinput;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.row.value.ValueMetaPluginType;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.TextFileInputFieldValidator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * @author Andrey Khayrutdinov
   * @deprecated replaced by implementation in the ...steps.fileinput.text package
 */
@Deprecated
public class TextFileInputMetaLoadSaveTest {
  @ClassRule public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

  private LoadSaveTester<TextFileInputMeta> tester;

  @BeforeClass
  public static void setUpBeforeClass() throws KettlePluginException {
    KettleLogStore.init();
    ValueMetaPluginType.getInstance().searchPlugins();
  }

  @Before
  public void setUp() throws Exception {
    List<String> commonAttributes = Arrays.asList(
      "fileName",
      "fileType",
      "separator",
      "enclosure",
      "escapeCharacter",
      "breakInEnclosureAllowed",
      "header",
      "nrHeaderLines",
      "footer",
      "nrFooterLines",
      "lineWrapped",
      "nrWraps",
      "layoutPaged",
      "nrLinesDocHeader",
      "nrLinesPerPage",
      "fileCompression",
      "noEmptyLines",
      "includeFilename",
      "filenameField",
      "includeRowNumber",
      "rowNumberByFile",
      "rowNumberField",
      "fileFormat",
      "rowLimit",
      "inputFields",
      "filter",
      "encoding",
      "errorIgnored",
      "errorCountField",
      "errorFieldsField",
      "errorTextField",
      "warningFilesDestinationDirectory",
      "warningFilesExtension",
      "errorFilesDestinationDirectory",
      "errorFilesExtension",
      "lineNumberFilesDestinationDirectory",
      "lineNumberFilesExtension",
      "dateFormatLenient",
      "dateFormatLocale",
      "errorLineSkipped",
      "acceptingFilenames",
      "passingThruFields",
      "acceptingField",
      "isaddresult",
      "shortFileFieldName",
      "pathFieldName",
      "hiddenFieldName",
      "lastModificationTimeFieldName",
      "uriNameFieldName",
      "rootUriNameFieldName",
      "extensionFieldName",
      "sizeFieldName",
      "skipBadFiles",
      "fileErrorField",
      "fileErrorMessageField"
    );
    List<String> xmlAttributes = Collections.emptyList();
    List<String> repoAttributes = Collections.emptyList();

    Map<String, String> getters = new HashMap<>();
    getters.put( "header", "hasHeader" );
    getters.put( "footer", "hasFooter" );
    getters.put( "noEmptyLines", "noEmptyLines" );
    getters.put( "includeFilename", "includeFilename" );
    getters.put( "includeRowNumber", "includeRowNumber" );
    getters.put( "errorFilesExtension", "getErrorLineFilesExtension" );
    getters.put( "isaddresult", "isAddResultFile" );
    getters.put( "shortFileFieldName", "getShortFileNameField" );
    getters.put( "pathFieldName", "getPathField" );
    getters.put( "hiddenFieldName", "isHiddenField" );
    getters.put( "lastModificationTimeFieldName", "getLastModificationDateField" );
    getters.put( "uriNameFieldName", "getUriField" );
    getters.put( "rootUriNameFieldName", "getRootUriField" );
    getters.put( "extensionFieldName", "getExtensionField" );
    getters.put( "sizeFieldName", "getSizeField" );

    Map<String, String> setters = new HashMap<>();
    setters.put( "fileName", "setFileNameForTest" );
    setters.put( "errorFilesExtension", "setErrorLineFilesExtension" );
    setters.put( "isaddresult", "setAddResultFile" );
    setters.put( "shortFileFieldName", "setShortFileNameField" );
    setters.put( "pathFieldName", "setPathField" );
    setters.put( "hiddenFieldName", "setIsHiddenField" );
    setters.put( "lastModificationTimeFieldName", "setLastModificationDateField" );
    setters.put( "uriNameFieldName", "setUriField" );
    setters.put( "rootUriNameFieldName", "setRootUriField" );
    setters.put( "extensionFieldName", "setExtensionField" );
    setters.put( "sizeFieldName", "setSizeField" );

    Map<String, FieldLoadSaveValidator<?>> attributeValidators = Collections.emptyMap();

    Map<String, FieldLoadSaveValidator<?>> typeValidators = new HashMap<>();
    typeValidators.put( TextFileFilter[].class.getCanonicalName(),
      new ArrayLoadSaveValidator<>( new TextFileFilterValidator() ) );
    typeValidators.put( TextFileInputField[].class.getCanonicalName(),
      new ArrayLoadSaveValidator<>( new TextFileInputFieldValidator() ) );

    assertTrue( !commonAttributes.isEmpty() || !( xmlAttributes.isEmpty() || repoAttributes.isEmpty() ) );

    tester =
      new LoadSaveTester<>( TextFileInputMeta.class, commonAttributes, xmlAttributes,
        repoAttributes, getters, setters, attributeValidators, typeValidators );
  }

  @Test
  public void testSerialization() throws Exception {
    tester.testSerialization();
  }

  private static class TextFileFilterValidator implements FieldLoadSaveValidator<TextFileFilter> {
    @Override public TextFileFilter getTestObject() {
      TextFileFilter fileFilter = new TextFileFilter();
      fileFilter.setFilterPosition( new Random().nextInt() );
      fileFilter.setFilterString( UUID.randomUUID().toString() );
      fileFilter.setFilterLastLine( new Random().nextBoolean() );
      fileFilter.setFilterPositive( new Random().nextBoolean() );
      return fileFilter;
    }

    @Override public boolean validateTestObject( TextFileFilter testObject, Object actual ) {
      if ( !( actual instanceof TextFileFilter ) ) {
        return false;
      }

      TextFileFilter another = (TextFileFilter) actual;
      return new EqualsBuilder()
        .append( testObject.getFilterPosition(), another.getFilterPosition() )
        .append( testObject.getFilterString(), another.getFilterString() )
        .append( testObject.isFilterLastLine(), another.isFilterLastLine() )
        .append( testObject.isFilterPositive(), another.isFilterPositive() )
        .isEquals();
    }
  }
}
