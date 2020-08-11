package com.jiubo.sam.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MenuCount {
    private List<MenuBean> menuBeanList;
    private List<Integer> checkedIdList;
    private List<Integer> firstIdList;
}
