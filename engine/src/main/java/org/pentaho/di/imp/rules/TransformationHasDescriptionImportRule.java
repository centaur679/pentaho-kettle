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


package org.pentaho.di.imp.rules;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.imp.rule.ImportRuleInterface;
import org.pentaho.di.imp.rule.ImportValidationFeedback;
import org.pentaho.di.imp.rule.ImportValidationResultType;
import org.pentaho.di.trans.TransMeta;
import org.w3c.dom.Node;

public class TransformationHasDescriptionImportRule extends BaseImportRule implements ImportRuleInterface {

  private int minLength;

  public TransformationHasDescriptionImportRule() {
    super();
    minLength = 20; // Default
  }

  @Override
  public String toString() {
    if ( minLength > 0 ) {
      return super.toString() + " The minimum length is " + minLength;
    } else {
      return super.toString();
    }
  }

  @Override
  public List<ImportValidationFeedback> verifyRule( Object subject ) {

    List<ImportValidationFeedback> feedback = new ArrayList<>();

    if ( !isEnabled() || !( subject instanceof TransMeta ) ) {
      return feedback;
    }

    TransMeta transMeta = (TransMeta) subject;
    String description = transMeta.getDescription();

    if ( null != description && minLength <= description.length() ) {
      feedback.add( new ImportValidationFeedback(
        this, ImportValidationResultType.APPROVAL, "A description is present" ) );
    } else {
      feedback.add( new ImportValidationFeedback(
        this, ImportValidationResultType.ERROR, "A description is not present or is too short." ) );
    }

    return feedback;
  }

  /**
   * @return the minLength
   */
  public int getMinLength() {
    return minLength;
  }

  /**
   * @param minLength
   *          the minLength to set
   */
  public void setMinLength( int minLength ) {
    this.minLength = minLength;
  }

  @Override
  public String getXML() {

    StringBuilder xml = new StringBuilder();
    xml.append( XMLHandler.openTag( XML_TAG ) );

    xml.append( super.getXML() ); // id, enabled

    xml.append( XMLHandler.addTagValue( "min_length", minLength ) );

    xml.append( XMLHandler.closeTag( XML_TAG ) );
    return xml.toString();
  }

  @Override
  public void loadXML( Node ruleNode ) throws KettleException {
    super.loadXML( ruleNode );

    minLength = Const.toInt( XMLHandler.getTagValue( ruleNode, "min_length" ), 0 );
  }
}
