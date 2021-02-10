package com.netsuite.webservices.samples;

import com.netsuite.suitetalk.client.v2020_1.WsClient;
import com.netsuite.suitetalk.proxy.v2020_1.platform.core.types.GetAllRecordType;

import org.apache.axis.AxisFault;

import java.io.IOException;
import java.net.MalformedURLException;

import static com.netsuite.webservices.samples.Messages.*;
import static com.netsuite.webservices.samples.utils.PrintUtils.printError;

/**
 * <p>
 * Fully functional, command-line driven application that illustrates how to
 * connect to the NetSuite web services and invoke operations.
 * </p>
 * <p>
 * Please see the README on how to compile and run. Note that the
 * {@code nsclient.properties} file must exist in the installed root directory
 * for this application to run.
 * </p>
 * <p>
 * © 2019 NetSuite Inc. All rights reserved.
 * </p>
 */
public class NetsuiteClient {

	public static void main(String[] args) {
		WsClient client = null;
		try {
			client = WsClientFactory.getWsClient(new Properties(), null);
			new NetsuiteOperations(client).run(GetAllRecordType.currency);
		} catch (AxisFault e) {
            printError(ERROR_OCCURRED, e.getFaultString());
            System.exit(3);
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
