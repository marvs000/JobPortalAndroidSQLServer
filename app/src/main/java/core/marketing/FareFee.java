package core.marketing;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

import core.marketing.util.MarketingObjectDetail;
import core.marketing.util.MarketingObjectType;
import core.marketing.util.MarketingObjectTypeClass;

/**
 * Created by ernestepistola on 2/4/17.
 */
@Deprecated
public class FareFee extends MarketingObjectTypeClass{

    public static final String DB_COLUMN_FEE = "FEE";
    public static final String DB_COLUMN_BUS_NAME = "BUS_NAME";

    public FareFee(String busName, Double fee){
        super(MarketingObjectType.FARE_FEE);
        /* Add details to this object */
        this.details.add(new MarketingObjectDetail(DB_COLUMN_BUS_NAME, busName));
        this.details.add(new MarketingObjectDetail(DB_COLUMN_FEE, String.format("%.2g%n", fee)));
    }

    public Double getFee(){
        DecimalFormat df = new DecimalFormat();
        try {
            return (Double) df.parse(this.getKeyValue(DB_COLUMN_FEE)).doubleValue();
        }catch(ParseException e){
            System.err.println("An error occured while trying to parse the fee for the FareFree object");
            return null;
        }
    }

    public void setFee(Double fee){
        this.setKeyValue(DB_COLUMN_FEE, String.format("%.2g%n", fee));
    }

    public String getBusName(){
        return getKeyValue(DB_COLUMN_BUS_NAME);
    }

    public void setBusName(String busName){
        setKeyValue(DB_COLUMN_BUS_NAME, busName);
    }

}
