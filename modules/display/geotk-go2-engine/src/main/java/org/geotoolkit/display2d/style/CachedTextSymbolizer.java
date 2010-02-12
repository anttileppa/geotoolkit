/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.display2d.style;

import org.geotoolkit.display2d.GO2Utilities;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Paint;
import java.awt.RenderingHints;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;

import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Fill;
import org.opengis.style.Halo;
import org.opengis.style.LabelPlacement;
import org.opengis.style.LinePlacement;
import org.opengis.style.PointPlacement;
import org.opengis.style.TextSymbolizer;

/**
 * Cached text symbolizer.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CachedTextSymbolizer extends CachedSymbolizer<TextSymbolizer>{

    //Cached values
    private final CachedFont cachedFont;
    private final CachedHalo cachedHalo;
    private final CachedFill cachedFill;
    private final CachedLabelPlacement cachedPlacement;
    
    private String label = null;

    public CachedTextSymbolizer(TextSymbolizer symbolizer,
            SymbolizerRenderer<TextSymbolizer,? extends CachedSymbolizer<TextSymbolizer>> renderer){
        super(symbolizer,renderer);
        
        final org.opengis.style.Font font = styleElement.getFont();
        cachedFont = CachedFont.cache(font);
        
        final Fill fill = styleElement.getFill();
        cachedFill = CachedFill.cache(fill);
            
        //halo can be null.
        final Halo halo = styleElement.getHalo();
        if(halo != null){
            cachedHalo = CachedHalo.cache(halo);
        }else{
            cachedHalo = null;
        }
        
        final LabelPlacement placement = styleElement.getLabelPlacement();
        if(placement instanceof PointPlacement){
            cachedPlacement = CachedPointPlacement.cache((PointPlacement) placement);
        }else if(placement instanceof LinePlacement){
            cachedPlacement = CachedLinePlacement.cache((LinePlacement) placement);
        }else{
            throw new IllegalArgumentException("A text symbolizer must have a placement set of type : PointPlacement or LinePlacement.");
        }
        
        styleElement.getFont();
//        symbol.getGeometryPropertyName();
        styleElement.getHalo();
        styleElement.getLabel();
        styleElement.getLabelPlacement();
        styleElement.getUnitOfMeasure();
        
    }

    public Paint getFontPaint(Feature feature, int x, int y, float coeff, RenderingHints hints){
        Paint paint;
                
        if(cachedFill != null){
            paint = cachedFill.getJ2DPaint(feature, x, y, coeff, hints);
        }else{
            paint = Color.BLACK;
        }
        
        return paint;
    }
    
    public Composite getFontComposite(Feature feature){
        Composite composite;
                
        if(cachedFill != null){
            composite = cachedFill.getJ2DComposite(feature);
        }else{
            composite = AlphaComposite.SrcOver;
        }
        
        return composite;
    }
    
    public Font getJ2dFont(Feature feature, float coeff){
        return cachedFont.getJ2dFont(feature, coeff);
    }
    
    public String getLabel(Feature feature){
        return GO2Utilities.evaluate(styleElement.getLabel(),feature,String.class, "Label");
    }
        
    public CachedHalo getHalo(){
        return cachedHalo;
    }
    
    public CachedLabelPlacement getPlacement(){
        return cachedPlacement;
    }
    
    @Override
    protected void evaluate() {
        if(!isNotEvaluated) return;
        
        final Expression expLabel = styleElement.getLabel();
        
        //we can not know so always visible
        isStaticVisible = VisibilityState.VISIBLE;
        
        if(GO2Utilities.isStatic(expLabel)){
            label = GO2Utilities.evaluate(expLabel, null, String.class, "No Label");
        }else{
            GO2Utilities.getRequieredAttributsName(expLabel,requieredAttributs);
            isStatic = false;
        }

        cachedFont.getRequieredAttributsName(requieredAttributs);
        cachedFill.getRequieredAttributsName(requieredAttributs);
        cachedHalo.getRequieredAttributsName(requieredAttributs);
        cachedPlacement.getRequieredAttributsName(requieredAttributs);
        
        //no attributs needed replace with static empty list.
        if(requieredAttributs.isEmpty()){
            requieredAttributs = EMPTY_ATTRIBUTS;
        }
        
        
        isNotEvaluated = false;
    }

    @Override
    public float getMargin(Feature feature, float coeff) {
        return 0f;
    }

    @Override
    public boolean isVisible(Feature feature) {
        return true;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public VisibilityState isStaticVisible() {
        return VisibilityState.DYNAMIC;
    }

}
