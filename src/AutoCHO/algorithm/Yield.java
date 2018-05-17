package AutoCHO.algorithm;
import AutoCHO.entity.DS_SugarStructure;
import AutoCHO.entity.DS_OptResidue;
import AutoCHO.entity.DS_BuildingBlock;
import AutoCHO.entity.DS_Library;
import AutoCHO.MainProcessor;
import java.util.*;

public class Yield {
    public static double CalcYield(List<Integer> BBList){
        double yield = 1.0;
        for (int i = 0; i < BBList.size() - 1; i++){
            int BBIdx1 = BBList.get(i);
            int BBIdx2 = BBList.get(i + 1);
            double RRV1 = MainProcessor.GetInstance().CombinedBBLList.get(BBIdx1).RRV;
            double RRV2 = MainProcessor.GetInstance().CombinedBBLList.get(BBIdx2).RRV;
            if(RRV1 < RRV2){
                yield = -1;
                break;
            }
            double stepwiseYield = FindYield(RRV1 / RRV2);
            yield *= stepwiseYield;
        }
        return yield;
    }
    
    public static double CalcTestYield(List<Double> RRVList){
        double yield = 1.0;
        for (int i = 0; i < RRVList.size() - 1; i++){
            double RRV1 = RRVList.get(i);
            double RRV2 = RRVList.get(i + 1);
            if(RRV1 < RRV2){
                yield = -1;
                break;
            }
            double stepwiseYield = FindYield(RRV1 / RRV2);
            yield *= stepwiseYield;
        }
        return yield;
    }
    
    public static boolean CheckRRVRatio(List<Integer> BBList){
        int DonorIdx = BBList.size() - 2;
        int AcceptorIdx = BBList.size() - 1;
        
        int BBIdx1 = BBList.get(DonorIdx);
        int BBIdx2 = BBList.get(AcceptorIdx);
        double RRV1 = MainProcessor.GetInstance().CombinedBBLList.get(BBIdx1).RRV;
        double RRV2 = MainProcessor.GetInstance().CombinedBBLList.get(BBIdx2).RRV;
        double MinDonorAcceptorRRVRatio = MainProcessor.GetInstance().MinDonorAcceptorRRVRatio;
        double MaxDonorAcceptorRRVRatio = MainProcessor.GetInstance().MaxDonorAcceptorRRVRatio;
        if(((RRV1 / RRV2) < MinDonorAcceptorRRVRatio) || ((RRV1 / RRV2) > MaxDonorAcceptorRRVRatio)){
            return false;
        }
        return true;
    }
    
    public static boolean CheckRRVDiff(List<Integer> BBList){
        int DonorIdx = BBList.size() - 2;
        int AcceptorIdx = BBList.size() - 1;
        
        int BBIdx1 = BBList.get(DonorIdx);
        int BBIdx2 = BBList.get(AcceptorIdx);
        double RRV1 = MainProcessor.GetInstance().CombinedBBLList.get(BBIdx1).RRV;
        double RRV2 = MainProcessor.GetInstance().CombinedBBLList.get(BBIdx2).RRV;
        double MinDonorAcceptorRRVDiff = MainProcessor.GetInstance().MinDonorAcceptorRRVDiff;
        if(Math.abs(RRV1 - RRV2) < MinDonorAcceptorRRVDiff){
            return false;
        }
        return true;
    }
    
    public static void CalcYield(DS_SugarStructure SS){
        for (int i = 0; i < SS.CandidateList.size(); i++){
            double yield = 1.0;
            List<DS_BuildingBlock> BBList = SS.CandidateList.get(i).BBList;
            for (int j = BBList.size() - 1; j >= 1; j--){
                double stepwiseYield = FindYield(BBList.get(j).RRV / BBList.get(j - 1).RRV);
                yield *= stepwiseYield;
            }
            SS.CandidateList.get(i).Yield = yield;
        }
        Collections.sort(SS.CandidateList, new CandidateYieldOrderByDescending());
    }
    
    public static void CalcFragYield(DS_SugarStructure SS){
        DS_Library library = MainProcessor.GetInstance().GetLibrary();
        for (int i = 0; i < SS.CandidateList.size(); i++)
        {
            int index = 0;
            double yield = 1.0;
            List<DS_BuildingBlock> BBList = new ArrayList<>();
            
            for (DS_BuildingBlock BB:SS.CandidateList.get(i).BBList){
                BBList.add(library.BBLList.get(BB.DBIdx - 1));
                for (DS_OptResidue sugar:BB.Opt_Glycan.node.values()){
                    BBList.get(BBList.size()-1).Opt_Glycan.node.firstEntry().getValue().DeprotectingCIDLink=sugar.DeprotectingCIDLink;
                }
            }
            List<List<DS_BuildingBlock>> fragBBList = new ArrayList<>();

            for (int j = BBList.size() - 1; j >= 0; j--){
                if (BBList.get(j).IsFullyProtected() == false && j != (BBList.size() - 1)){
                    ++index;
                }
                if (BBList.get(j).IsFullyProtected() == false){
                    List<DS_BuildingBlock> newBBList = new ArrayList<>();
                    newBBList.add(BBList.get(j).Clone());
                    fragBBList.add(newBBList);
                }
                else{
                    fragBBList.get(index).add(BBList.get(j).Clone());
                }
            }

            if (fragBBList.size() == 1){
                for (int j = BBList.size() - 1; j >= 1; j--){
                    DS_BuildingBlock donor = BBList.get(j);
                    DS_BuildingBlock acceptor = BBList.get(j - 1);
                    double stepwiseYield = FindYield(donor.RRV / acceptor.RRV);
                    yield *= stepwiseYield;
                }
            }
            else{
                for (int j = 0; j <= fragBBList.size() - 2; j++){
                    DS_BuildingBlock donor = fragBBList.get(j).get(fragBBList.get(j).size() - 1);
                    DS_BuildingBlock acceptor = fragBBList.get(j + 1).get(fragBBList.get(j + 1).size() - 1);
                    double stepwiseYield = FindYield(donor.RRV / acceptor.RRV);
                    yield *= stepwiseYield;
                }
            }
            SS.CandidateList.get(i).FragBBList = fragBBList;
            SS.CandidateList.get(i).Yield = yield;
            SS.CandidateList.get(i).Steps = fragBBList.size();
        }
        Collections.sort(SS.CandidateList, new CandidateYieldOrderByDescending());
    }
    
    public static double CalcTheoBestCase(DS_SugarStructure sugars){
        double theoBestYield = 1;
        return theoBestYield;
    }
    
    private static double FindYield(double rateRatio){
        double yield = rateRatio / ((double)1 + rateRatio);
        double highYield = (yield + (double)1) / (double)2;
        double lowYield = 2 * yield - highYield;

        if (lowYield < yield / 2){
            lowYield = yield / 2;
        }

        if (highYield > yield * 2){
            highYield = yield * 2;
        }

        double yieldError = GetErrorEqn(rateRatio, yield);
        double highError = GetErrorEqn(rateRatio, highYield);
        double lowError = GetErrorEqn(rateRatio, lowYield);
        double fractionalPrecision = 0.0000001;

        double B = 0.0;
        double C = 0.0;
        double oldYield = 0.0;

        do{
            oldYield = yield;

            C = ((lowError - highError) / (highYield - lowYield) + (lowError - yieldError) / (lowYield - yield)) / (yield - highYield);
            B = (yieldError - lowError) / (yield - lowYield) - C * (lowYield + yield);
            yield = -0.5 * B / C;

            if (yield < oldYield){
                if (yield < lowYield){
                    lowYield -= oldYield - yield;
                    if (lowYield <= 0){
                        lowYield = yield / 2;
                    }
                }
                else{
                    highYield = oldYield;
                }

                if (yield == lowYield){
                    lowYield *= 0.9;
                }
            }
            else{
                if (yield > highYield){
                    highYield += yield - oldYield;
                    if (highYield >= 1){
                        highYield = (yield + 1) / 2;
                    }
                }
                else{
                    lowYield = oldYield;
                }

                if (yield == highYield){
                    highYield += (1 - yield) * 0.1;
                }
            }

            yieldError = GetErrorEqn(rateRatio, yield);
            highError  = GetErrorEqn(rateRatio, highYield);
            lowError   = GetErrorEqn(rateRatio, lowYield);
        }
        while (Math.abs(oldYield - yield) / yield > fractionalPrecision);
        return yield;
    }
    
    private static double GetErrorEqn(double ratio, double yield){
        double equation = Math.pow(yield, ratio) + yield - (double)1.0;
        return equation * equation; 
    }
}
