package com.takeatrip.Interfaces;

import com.takeatrip.Classes.Profilo;
import com.takeatrip.Classes.Tappa;

import java.util.List;
import java.util.Map;

/**
 * Created by lucagiacomelli on 10/03/16.
 */
public interface AsyncResponseStops {
    void processFinishForStops(Map<Profilo,List<Tappa>> profilo_tappe);

}
