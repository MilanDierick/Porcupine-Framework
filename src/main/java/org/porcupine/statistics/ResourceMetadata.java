/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.statistics;

import init.resources.RESOURCE;

/**
 * @author Milan Dierick
 */
public class ResourceMetadata {
	private RESOURCE resource;
	private int buyPrice;
	private int sellPrice;
	private int stockpile;
	
	/**
	 * @param resource  The resource this metadata is for.
	 * @param buyPrice  The buy price of the resource.
	 * @param sellPrice The sell price of the resource.
	 * @param stockpile The amount of the resource in stockpile.
	 */
	public ResourceMetadata(RESOURCE resource, int buyPrice, int sellPrice, int stockpile) {
		this.resource = resource;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.stockpile = stockpile;
	}
	
	public RESOURCE getResource() {
		return resource;
	}
	
	public int getBuyPrice() {
		return buyPrice;
	}
	
	public int getSellPrice() {
		return sellPrice;
	}
	
	public int getStockpile() {
		return stockpile;
	}
	
	public void setResource(RESOURCE resource) {
		this.resource = resource;
	}
	
	public void setBuyPrice(int buyPrice) {
		this.buyPrice = buyPrice;
	}
	
	public void setSellPrice(int sellPrice) {
		this.sellPrice = sellPrice;
	}
	
	public void setStockpile(int stockpile) {
		this.stockpile = stockpile;
	}
	
	@Override
	public String toString() {
		return "ResourceMetadata{" + "resource=" + resource + ", buyPrice=" + buyPrice + ", sellPrice=" + sellPrice + ", stockpile=" + stockpile + '}';
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		ResourceMetadata other = (ResourceMetadata) obj;
		
		if (buyPrice != other.buyPrice)
			return false;
		if (sellPrice != other.sellPrice)
			return false;
		if (stockpile != other.stockpile)
			return false;
		return resource.equals(other.resource);
	}
	
	@Override
	public int hashCode() {
		int result = resource.hashCode();
		result = 31 * result + buyPrice;
		result = 31 * result + sellPrice;
		result = 31 * result + stockpile;
		return result;
	}
}
