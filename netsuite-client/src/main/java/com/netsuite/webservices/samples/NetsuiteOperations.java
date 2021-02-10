package com.netsuite.webservices.samples;

import com.netsuite.suitetalk.client.v2020_1.WsClient;
import com.netsuite.suitetalk.client.v2020_1.utils.Utils;
import com.netsuite.suitetalk.proxy.v2020_1.documents.filecabinet.File;
import com.netsuite.suitetalk.proxy.v2020_1.documents.filecabinet.types.FileAttachFrom;
import com.netsuite.suitetalk.proxy.v2020_1.lists.accounting.InventoryItem;
import com.netsuite.suitetalk.proxy.v2020_1.lists.accounting.Price;
import com.netsuite.suitetalk.proxy.v2020_1.lists.accounting.PriceList;
import com.netsuite.suitetalk.proxy.v2020_1.lists.accounting.Pricing;
import com.netsuite.suitetalk.proxy.v2020_1.lists.accounting.PricingMatrix;
import com.netsuite.suitetalk.proxy.v2020_1.lists.accounting.types.ItemCostingMethod;
import com.netsuite.suitetalk.proxy.v2020_1.lists.relationships.Customer;
import com.netsuite.suitetalk.proxy.v2020_1.lists.relationships.CustomerAddressbook;
import com.netsuite.suitetalk.proxy.v2020_1.lists.relationships.CustomerAddressbookList;
import com.netsuite.suitetalk.proxy.v2020_1.lists.relationships.CustomerSearch;
import com.netsuite.suitetalk.proxy.v2020_1.lists.relationships.CustomerSearchAdvanced;
import com.netsuite.suitetalk.proxy.v2020_1.lists.relationships.CustomerSearchRow;
import com.netsuite.suitetalk.proxy.v2020_1.lists.relationships.types.EmailPreference;
import com.netsuite.suitetalk.proxy.v2020_1.platform.common.Address;
import com.netsuite.suitetalk.proxy.v2020_1.platform.common.CustomRecordSearchBasic;
import com.netsuite.suitetalk.proxy.v2020_1.platform.common.CustomerSearchBasic;
import com.netsuite.suitetalk.proxy.v2020_1.platform.common.CustomerSearchRowBasic;
import com.netsuite.suitetalk.proxy.v2020_1.platform.common.TransactionSearchBasic;
import com.netsuite.suitetalk.proxy.v2020_1.platform.common.TransactionSearchRowBasic;
import com.netsuite.suitetalk.proxy.v2020_1.platform.common.types.Country;
import com.netsuite.suitetalk.proxy.v2020_1.platform.core.*;
import com.netsuite.suitetalk.proxy.v2020_1.platform.core.types.GetAllRecordType;
import com.netsuite.suitetalk.proxy.v2020_1.platform.core.types.InitializeRefType;
import com.netsuite.suitetalk.proxy.v2020_1.platform.core.types.InitializeType;
import com.netsuite.suitetalk.proxy.v2020_1.platform.core.types.RecordType;
import com.netsuite.suitetalk.proxy.v2020_1.platform.core.types.SearchEnumMultiSelectFieldOperator;
import com.netsuite.suitetalk.proxy.v2020_1.platform.core.types.SearchMultiSelectFieldOperator;
import com.netsuite.suitetalk.proxy.v2020_1.platform.core.types.SearchStringFieldOperator;
import com.netsuite.suitetalk.proxy.v2020_1.platform.messages.ReadResponse;
import com.netsuite.suitetalk.proxy.v2020_1.platform.messages.ReadResponseList;
import com.netsuite.suitetalk.proxy.v2020_1.platform.messages.WriteResponse;
import com.netsuite.suitetalk.proxy.v2020_1.platform.messages.WriteResponseList;
import com.netsuite.suitetalk.proxy.v2020_1.setup.customization.CustomRecord;
import com.netsuite.suitetalk.proxy.v2020_1.transactions.sales.ItemFulfillment;
import com.netsuite.suitetalk.proxy.v2020_1.transactions.sales.SalesOrder;
import com.netsuite.suitetalk.proxy.v2020_1.transactions.sales.SalesOrderItem;
import com.netsuite.suitetalk.proxy.v2020_1.transactions.sales.SalesOrderItemList;
import com.netsuite.suitetalk.proxy.v2020_1.transactions.sales.TransactionSearch;
import com.netsuite.suitetalk.proxy.v2020_1.transactions.sales.TransactionSearchAdvanced;
import com.netsuite.suitetalk.proxy.v2020_1.transactions.sales.TransactionSearchRow;
import com.netsuite.suitetalk.proxy.v2020_1.transactions.sales.types.SalesOrderOrderStatus;
import com.netsuite.webservices.samples.utils.PrintUtils;
import org.apache.axis.AxisFault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.netsuite.suitetalk.client.common.utils.CommonUtils.isEmptyString;
import static com.netsuite.suitetalk.client.v2020_1.utils.Utils.createRecordRef;
import static com.netsuite.webservices.samples.Messages.*;
import static com.netsuite.webservices.samples.ResponseHandler.*;
import static com.netsuite.webservices.samples.io.Console.readLine;
import static com.netsuite.webservices.samples.utils.IndentationUtils.getIndentedString;
import static com.netsuite.webservices.samples.utils.ParsingUtils.getErrorMessage;
import static com.netsuite.webservices.samples.utils.PrintUtils.*;
import static com.netsuite.webservices.samples.utils.StringUtils.getBoolean;
import static com.netsuite.webservices.samples.utils.StringUtils.getListItems;
import static com.netsuite.webservices.samples.utils.StringUtils.getRandomString;
import static java.lang.String.format;

/**
 * <p>Displays a list of all sample operations and invokes the selected operation by the user.</p>
 * <p>© 2019 NetSuite Inc. All rights reserved.</p>
 */
@ParametersAreNonnullByDefault
public class NetsuiteOperations {

    private static final int DEFAULT_PAGE_SIZE = 1000;
    private static final int PAGE_SIZE = 10;

    private static final Map<String, Operation> SAMPLE_OPERATIONS = new LinkedHashMap<>();

    private WsClient client;

    /**
     * Constructor initializing a list of all sample operations.
     *
     * @param client Client used for all SOAP requests
     */
    public NetsuiteOperations(WsClient client) {
        this.client = client;
    }

    /**
     * Starts selection of sample operation.
     * @param recordType 
     */
    public void run(GetAllRecordType recordType) throws Exception{   
    	printInfo("Waiting for "+recordType.getValue());
    	getRecordList(recordType);
    	
    }

    
    private void getRecordList(GetAllRecordType recordType) throws Exception{      

            GetAllRecordType selectedRecordType = recordType;
            printSendingRequestMessage();

            // Invoke getAll() operation
            GetAllResult allRerults = client.callGetAllRecords(selectedRecordType);

            if(! allRerults.getStatus().isIsSuccess()) {
                printError(allRerults.getStatus().getStatusDetail(0).getMessage());
                return;
            }
            List<Record> allRecords = Arrays.asList(allRerults.getRecordList().getRecord());

            if (allRecords == null || allRecords.isEmpty()) {
                printWithEmptyLine(NO_RECORDS_FOUND);
            } else {
                
                printWithEmptyLine(REQUESTED_LIST_OF_RECORDS, recordType.getValue(), allRecords.size());
                allRecords.forEach(PrintUtils::printGetAllRecord);
            }
       
    	
    }

    
    private boolean isSuccessfulSearchResult(SearchResult searchResult) {
        return searchResult.getStatus() != null && searchResult.getStatus().isIsSuccess();
    }
}
