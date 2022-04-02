/*
 * @file : com.project.batch.domain.common.util.CommonUtil.java
 * @desc : Java의 공통 기능을 정의해 놓은 Util 클래스(Object Null 값 체크 등)
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.common.util;

import java.util.List;
import java.util.Map;

public class CommonUtil {

    /*
     * @method : isEmpty
     * @desc : 공백 OR NULL값 체크
     * @param :
     * @return :
     * */
    public static boolean isEmpty(Object obj) {
        if (obj == null) return true;
        if ((obj instanceof String) && (((String)obj).trim().length() == 0)) return true;
        if (obj instanceof Map) return ((Map<?, ?>) obj).isEmpty();
        if (obj instanceof List) return ((List<?>) obj).isEmpty();
        if (obj instanceof Object[]) return ((Object[]) obj).length == 0;
        return false;
    }
}
