package core.marketing.util;

import java.util.ArrayList;
import java.util.List;

import core.marketing.interfaces.IMarketingObject;

/**
 * Created by ernestepistola on 2/4/17.
 */
public class MarketingObjectTypeClass implements IMarketingObject{


    private MarketingObjectType type;
    protected List<MarketingObjectDetail> details;


    public MarketingObjectTypeClass(MarketingObjectType type){
        this.type = type;
        this.details = new ArrayList<MarketingObjectDetail>();
    }


    @Override
    public MarketingObjectType getMarketingObjectType() {
        return this.type;
    }

    @Override
    public List<MarketingObjectDetail> getAdvertismentDetails() {
        return this.details;
    }

    protected void setKeyValue(String key, String value){

        for(MarketingObjectDetail detail: this.details){
            if(detail.getKey().equals(key)){
                detail.setValue(value);
            }
        }

    }

    protected String getKeyValue(String key){

        for(MarketingObjectDetail detail: this.details){
            if(detail.getKey().equals(key)){
                return detail.getValue();
            }
        }

        return null;

    }

}
