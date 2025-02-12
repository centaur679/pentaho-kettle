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


package org.pentaho.di.trans.steps.userdefinedjavaclass;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Pattern;

import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaInternetAddress;
import org.pentaho.di.core.row.value.ValueMetaTimestamp;
import org.pentaho.di.i18n.BaseMessages;

public class FieldHelper {
  private int index = -1;
  private ValueMetaInterface meta;

  private static Class<?> PKG = FieldHelper.class; // for i18n purposes, needed by Translator2!!

  public FieldHelper( RowMetaInterface rowMeta, String fieldName ) {
    this.meta = rowMeta.searchValueMeta( fieldName );
    this.index = rowMeta.indexOfValue( fieldName );
    if ( this.index == -1 ) {
      throw new IllegalArgumentException( String.format(
        "FieldHelper could not be initialized. The field named '%s' not found in RowMeta: %s", fieldName,
        rowMeta.toStringMeta() ) );
    }
  }

  public Object getObject( Object[] dataRow ) {
    return dataRow[index];
  }

  @Deprecated
  public BigDecimal getBigNumber( Object[] dataRow ) throws KettleValueException {
    return getBigDecimal( dataRow );
  }

  public BigDecimal getBigDecimal( Object[] dataRow ) throws KettleValueException {
    return meta.getBigNumber( dataRow[index] );
  }

  public byte[] getBinary( Object[] dataRow ) throws KettleValueException {
    return meta.getBinary( dataRow[index] );
  }

  public Boolean getBoolean( Object[] dataRow ) throws KettleValueException {
    return meta.getBoolean( dataRow[index] );
  }

  public Date getDate( Object[] dataRow ) throws KettleValueException {
    return meta.getDate( dataRow[index] );
  }

  @Deprecated
  public Long getInteger( Object[] dataRow ) throws KettleValueException {
    return getLong( dataRow );
  }

  public Long getLong( Object[] dataRow ) throws KettleValueException {
    return meta.getInteger( dataRow[index] );
  }

  @Deprecated
  public Double getNumber( Object[] dataRow ) throws KettleValueException {
    return getDouble( dataRow );
  }

  public Double getDouble( Object[] dataRow ) throws KettleValueException {
    return meta.getNumber( dataRow[index] );
  }

  public Timestamp getTimestamp( Object[] dataRow ) throws KettleValueException {
    return  ( (ValueMetaTimestamp) meta ).getTimestamp( dataRow[index] );
  }

  public InetAddress getInetAddress( Object[] dataRow ) throws KettleValueException {
    return  ( (ValueMetaInternetAddress) meta ).getInternetAddress( dataRow[index] );
  }

  public Serializable getSerializable( Object[] dataRow ) throws KettleValueException {
    return (Serializable) dataRow[index];
  }

  public String getString( Object[] dataRow ) throws KettleValueException {
    return meta.getString( dataRow[index] );
  }

  public ValueMetaInterface getValueMeta() {
    return meta;
  }

  public int indexOfValue() {
    return index;
  }

  public void setValue( Object[] dataRow, Object value ) {
    dataRow[index] = value;
  }

  public void setValue( Object[] dataRow, byte[] value ) {
    dataRow[index] = value;
  }

  private static final Pattern validJavaIdentifier = Pattern.compile( "^[\\w&&\\D]\\w*" );

  public static String getAccessor( boolean isIn, String fieldName ) {
    StringBuilder sb = new StringBuilder( "get(Fields." );
    sb.append( isIn ? "In" : "Out" );
    sb.append( String.format( ", \"%s\")", fieldName.replace( "\\", "\\\\" ).replace( "\"", "\\\"" ) ) );
    return sb.toString();
  }

  public static String getGetSignature( String accessor, ValueMetaInterface v ) {
    StringBuilder sb = new StringBuilder();

    switch ( v.getType() ) {
      case ValueMetaInterface.TYPE_BIGNUMBER:
        sb.append( "BigDecimal " );
        break;
      case ValueMetaInterface.TYPE_BINARY:
        sb.append( "byte[] " );
        break;
      case ValueMetaInterface.TYPE_BOOLEAN:
        sb.append( "Boolean " );
        break;
      case ValueMetaInterface.TYPE_DATE:
        sb.append( "Date " );
        break;
      case ValueMetaInterface.TYPE_INTEGER:
        sb.append( "Long " );
        break;
      case ValueMetaInterface.TYPE_NUMBER:
        sb.append( "Double " );
        break;
      case ValueMetaInterface.TYPE_STRING:
        sb.append( "String " );
        break;
      case ValueMetaInterface.TYPE_INET:
        sb.append( "InetAddress " );
        break;
      case ValueMetaInterface.TYPE_TIMESTAMP:
        sb.append( "Timestamp " );
        break;
      case ValueMetaInterface.TYPE_SERIALIZABLE:
      default:
        sb.append( "Object " );
        break;
    }

    if ( validJavaIdentifier.matcher( v.getName() ).matches() ) {
      sb.append( v.getName() );
    } else {
      sb.append( "value" );
    }
    String name = getNativeDataTypeSimpleName( v );
    sb
      .append( " = " ).append( accessor ).append( ".get" ).append( "-".equals( name ) ? "Object" : name )
      .append( "(r);" );

    return sb.toString();
  }

  public static String getNativeDataTypeSimpleName( ValueMetaInterface v ) {
    try {
      return v.getType() != ValueMetaInterface.TYPE_BINARY ? v.getNativeDataTypeClass().getSimpleName() : "Binary";
    } catch ( KettleValueException e ) {
      LogChannelInterface log = new LogChannel( v );
      log.logDebug( BaseMessages.getString( PKG, "FieldHelper.Log.UnknownNativeDataTypeSimpleName" ) );
      return "Object";
    }
  }
}
