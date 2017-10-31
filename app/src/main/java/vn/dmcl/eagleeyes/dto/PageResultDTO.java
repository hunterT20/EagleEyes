package vn.dmcl.eagleeyes.dto;

import java.util.List;

public class PageResultDTO<T> {
    public List<T> Items;
    public int Total;
    public int PageIndex;
    public int PageSize;
    public boolean Result;
    public int Code;
    public String Message;
}
