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

package org.pentaho.di.job.entries.webserviceavailable;

import java.util.Arrays;

import java.util.List;
import java.util.Map;

import org.junit.ClassRule;
import org.pentaho.di.job.entry.loadSave.JobEntryLoadSaveTestSupport;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;

public class JobEntryWebServiceAvailableTest extends JobEntryLoadSaveTestSupport<JobEntryWebServiceAvailable> {
  @ClassRule public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

  @Override
  protected Class<JobEntryWebServiceAvailable> getJobEntryClass() {
    return JobEntryWebServiceAvailable.class;
  }

  @Override
  protected List<String> listCommonAttributes() {
    return Arrays.asList(
        "url",
        "connectTimeOut",
        "readTimeOut" );
  }

  @Override
  protected Map<String, String> createGettersMap() {
    return toMap(
        "url", "getURL",
        "connectTimeOut", "getConnectTimeOut",
        "readTimeOut", "getReadTimeOut" );
  }

  @Override
  protected Map<String, String> createSettersMap() {
    return toMap(
        "url", "setURL",
        "connectTimeOut", "setConnectTimeOut",
        "readTimeOut", "setReadTimeOut" );
  }

}
