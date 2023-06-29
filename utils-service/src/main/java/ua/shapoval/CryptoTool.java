package ua.shapoval;


import org.hashids.Hashids;


public class CryptoTool {
    private final Hashids hashids;

    public CryptoTool(String hashValue) {
       var minHashLength = 10;
        this.hashids = new Hashids(hashValue, minHashLength);
    }
    public String hashOf(Long value){
        return hashids.encode(value);
    }
    public Long idOf(String value){
        long[] result = hashids.decode(value);
        if (result != null && result.length > 0){
            return result[0];
        }
        return null;
    }
}
