package com.wsk.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderForm implements Serializable {
    private Integer id;

    private Date modified;

    private Integer display;

    private Integer uid;

    private String context;
      
    private BigDecimal price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getModified() {
        return modified == null ? null : (Date) modified.clone();
    }

    public void setModified(Date modified) {
        this.modified = modified == null ? null : (Date) modified.clone();
    }

    public Integer getDisplay() {
        return display;
    }

    public void setDisplay(Integer display) {
        this.display = display;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getContext() {
        return context;
    }

    public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setContext(String context) {
        this.context = context == null ? null : context.trim();
    }
    
}