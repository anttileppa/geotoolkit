/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ogc.xml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.geotoolkit.gml.xml.v311.EnvelopeEntry;
import org.geotoolkit.ogc.xml.v110.AbstractIdType;
import org.geotoolkit.ogc.xml.v110.BinaryOperatorType;
import org.geotoolkit.ogc.xml.v110.ComparisonOpsType;
import org.geotoolkit.ogc.xml.v110.FeatureIdType;
import org.geotoolkit.ogc.xml.v110.FunctionType;
import org.geotoolkit.ogc.xml.v110.GmlObjectIdType;
import org.geotoolkit.ogc.xml.v110.LiteralType;
import org.geotoolkit.ogc.xml.v110.LogicOpsType;
import org.geotoolkit.ogc.xml.v110.PropertyNameType;
import org.geotoolkit.ogc.xml.v110.SpatialOpsType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.identity.Identifier;

/**
 * Transform OGC jaxb xml in GT classes.
 *
 * @author Johann Sorel (Geomatys)
 */
public class OGC110toGTTransformer {

    protected final FilterFactory2 filterFactory;

    public OGC110toGTTransformer(FilterFactory2 factory) {
        this.filterFactory = factory;
    }

    /**
     * Transform a SLD filter v1.1 in GT filter.
     */
    public Filter visitFilter(org.geotoolkit.ogc.xml.v110.FilterType ft){
        if(ft == null)return null;        
        
        if(ft.getComparisonOps() != null){
            final JAXBElement<? extends org.geotoolkit.ogc.xml.v110.ComparisonOpsType> jax = ft.getComparisonOps();
            return visitComparisonOp(jax);     
        }else if(ft.getLogicOps() != null){
            final JAXBElement<? extends org.geotoolkit.ogc.xml.v110.LogicOpsType> jax = ft.getLogicOps();
            return visitLogicOp(jax);
        }else if(ft.getSpatialOps() != null){
            final JAXBElement<? extends org.geotoolkit.ogc.xml.v110.SpatialOpsType> jax = ft.getSpatialOps();
            return visitSpatialOp(jax);
        }else if(ft.getId() != null && !ft.getId().isEmpty()){
            return visitIds(ft.getId());
        }else{
            //this case should not happen but if so, we consider it's an ALL features filter
            return Filter.INCLUDE;
        }
        
    }
    
    /**
     * Transform a SLD spatial Filter v1.1 in GT filter.
     */
    public Filter visitSpatialOp(final JAXBElement<? extends org.geotoolkit.ogc.xml.v110.SpatialOpsType> jax) {
        final org.geotoolkit.ogc.xml.v110.SpatialOpsType ops = jax.getValue();
        final String OpName = jax.getName().getLocalPart();

        if (ops instanceof org.geotoolkit.ogc.xml.v110.BinarySpatialOpType) {
            org.geotoolkit.ogc.xml.v110.BinarySpatialOpType binary = (org.geotoolkit.ogc.xml.v110.BinarySpatialOpType) ops;
            JAXBElement<? extends AbstractGeometryType> geom = binary.getAbstractGeometry();
            org.geotoolkit.ogc.xml.v110.PropertyNameType pnt = binary.getPropertyName().getValue();
                        
            Expression left = filterFactory.property(pnt.getContent());
            Expression right = visit(geom);

            if (OGCJAXBStatics.FILTER_SPATIAL_CONTAINS.equalsIgnoreCase(OpName)) {
                return filterFactory.contains(left,right);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_CROSSES.equalsIgnoreCase(OpName)) {
                return filterFactory.crosses(left,right);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_DISJOINT.equalsIgnoreCase(OpName)) {
                return filterFactory.disjoint(left,right);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_EQUALS.equalsIgnoreCase(OpName)) {
                return filterFactory.equal(left,right);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_INTERSECTS.equalsIgnoreCase(OpName)) {
                return filterFactory.intersects(left,right);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_OVERLAPS.equalsIgnoreCase(OpName)) {
                return filterFactory.overlaps(left,right);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_TOUCHES.equalsIgnoreCase(OpName)) {
                return filterFactory.touches(left,right);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_WITHIN.equalsIgnoreCase(OpName)) {
                return filterFactory.within(left,right);
            }
            
            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);
            
        } else if (ops instanceof org.geotoolkit.ogc.xml.v110.DistanceBufferType) {
            org.geotoolkit.ogc.xml.v110.DistanceBufferType dstOp = (org.geotoolkit.ogc.xml.v110.DistanceBufferType) ops;
            org.geotoolkit.ogc.xml.v110.DistanceType dt = dstOp.getDistanceType();
            JAXBElement<? extends AbstractGeometryType> geom = dstOp.getAbstractGeometry();
            org.geotoolkit.ogc.xml.v110.PropertyNameType pnt = dstOp.getPropertyName();

            Expression geom1 = filterFactory.property(pnt.getContent());
            Expression geom2 = visit(geom);
            //TODO marche pas ? ou est la distance ? Double.valueOf(dt.getContent());
            double distance = 0;
            String units = dt.getUnits();
            
            
            if (OGCJAXBStatics.FILTER_SPATIAL_DWITHIN.equalsIgnoreCase(OpName)) {
                return filterFactory.dwithin(geom1, geom2, distance, units);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_BEYOND.equalsIgnoreCase(OpName)) {
                return filterFactory.beyond(geom1, geom2, distance, units);
            }
            
            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);

        } else if (ops instanceof org.geotoolkit.ogc.xml.v110.BBOXType) {
            org.geotoolkit.ogc.xml.v110.BBOXType binary = (org.geotoolkit.ogc.xml.v110.BBOXType) ops;
            EnvelopeEntry box = binary.getEnvelope();
            String pnt = binary.getPropertyName();
            
            Expression geom = filterFactory.property(pnt);
            double minx = box.getPos().get(0).getOrdinate(0);
            double maxx = box.getPos().get(0).getOrdinate(1);
            double miny = box.getPos().get(1).getOrdinate(0);
            double maxy = box.getPos().get(1).getOrdinate(1);
            String srs =  box.getSrsName();
            
            if (OGCJAXBStatics.FILTER_SPATIAL_BBOX.equalsIgnoreCase(OpName)) {
                return filterFactory.bbox(geom, minx, miny, maxx, maxy, srs);
            }
            
            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);

        }

        throw new IllegalArgumentException("Unknowed filter element" + jax);
    }
    
    /**
     * Transform a SLD logic Filter v1.1 in GT filter.
     */
    public Filter visitLogicOp(final JAXBElement<? extends org.geotoolkit.ogc.xml.v110.LogicOpsType> jax) {
        final org.geotoolkit.ogc.xml.v110.LogicOpsType ops = jax.getValue();
        final String OpName = jax.getName().getLocalPart();

        if (ops instanceof org.geotoolkit.ogc.xml.v110.UnaryLogicOpType) {
            org.geotoolkit.ogc.xml.v110.UnaryLogicOpType unary = (org.geotoolkit.ogc.xml.v110.UnaryLogicOpType) ops;

            if (OGCJAXBStatics.FILTER_LOGIC_NOT.equalsIgnoreCase(OpName)) {
                Filter filter = null;
                
                if(unary.getComparisonOps() != null) filter = visitComparisonOp(unary.getComparisonOps());
                else if(unary.getLogicOps() != null) filter = visitLogicOp(unary.getLogicOps());
                else if(unary.getSpatialOps() != null) filter = visitSpatialOp(unary.getSpatialOps());
                
                if(filter == null){
                    throw new IllegalArgumentException("Invalide filter element" + unary);
                }
                
                return filterFactory.not(filter);
            }

        } else if (ops instanceof org.geotoolkit.ogc.xml.v110.BinaryLogicOpType) {
            org.geotoolkit.ogc.xml.v110.BinaryLogicOpType binary = (org.geotoolkit.ogc.xml.v110.BinaryLogicOpType) ops;

            if (OGCJAXBStatics.FILTER_LOGIC_AND.equalsIgnoreCase(OpName)) {
                final List<Filter> filters = new ArrayList<Filter>();

                for (JAXBElement<? extends ComparisonOpsType> ele : binary.getComparisonOps()) {
                    final Object value = ele.getValue();
                    if (value instanceof ComparisonOpsType) {
                        filters.add(visitComparisonOp((JAXBElement<? extends ComparisonOpsType>) value));
                    }
                }
                for (JAXBElement<? extends LogicOpsType> ele : binary.getLogicOps()) {
                    final Object value = ele.getValue();
                    if (value instanceof LogicOpsType) {
                        filters.add(visitLogicOp((JAXBElement<? extends LogicOpsType>) value));
                    }
                }
                for (JAXBElement<? extends SpatialOpsType> ele : binary.getSpatialOps()) {
                    final Object value = ele.getValue();
                    if (value instanceof SpatialOpsType) {
                        filters.add(visitSpatialOp((JAXBElement<? extends SpatialOpsType>) value));
                    }
                }
                
                return filterFactory.and(filters);
            } else if (OGCJAXBStatics.FILTER_LOGIC_OR.equalsIgnoreCase(OpName)) {
                final List<Filter> filters = new ArrayList<Filter>();
                
                for (JAXBElement<? extends ComparisonOpsType> ele : binary.getComparisonOps()) {
                    final Object value = ele.getValue();
                    if (value instanceof ComparisonOpsType) {
                        filters.add(visitComparisonOp((JAXBElement<? extends ComparisonOpsType>) value));
                    }
                }
                for (JAXBElement<? extends LogicOpsType> ele : binary.getLogicOps()) {
                    final Object value = ele.getValue();
                    if (value instanceof LogicOpsType) {
                        filters.add(visitLogicOp((JAXBElement<? extends LogicOpsType>) value));
                    }
                }
                for (JAXBElement<? extends SpatialOpsType> ele : binary.getSpatialOps()) {
                    final Object value = ele.getValue();
                    if (value instanceof SpatialOpsType) {
                        filters.add(visitSpatialOp((JAXBElement<? extends SpatialOpsType>) value));
                    }
                }
                
                return filterFactory.or(filters);
            }

        }

        throw new IllegalArgumentException("Unknowed filter element" + jax);
    }
    
    /**
     * Transform a SLD comparison Filter v1.1 in GT filter.
     */
    public Filter visitComparisonOp(final JAXBElement<? extends org.geotoolkit.ogc.xml.v110.ComparisonOpsType> jax){
        final org.geotoolkit.ogc.xml.v110.ComparisonOpsType ops = jax.getValue();
        final String OpName = jax.getName().getLocalPart();

        if (ops instanceof org.geotoolkit.ogc.xml.v110.BinaryComparisonOpType) {
            org.geotoolkit.ogc.xml.v110.BinaryComparisonOpType binary = (org.geotoolkit.ogc.xml.v110.BinaryComparisonOpType) ops;

            Expression left = visitExpression(binary.getExpression().get(0));
            Expression right = visitExpression(binary.getExpression().get(1));
            
            if (OGCJAXBStatics.FILTER_COMPARISON_ISEQUAL.equalsIgnoreCase(OpName)) {
                return filterFactory.equals(left,right);
            } else if (OGCJAXBStatics.FILTER_COMPARISON_ISNOTEQUAL.equalsIgnoreCase(OpName)) {
                return filterFactory.notEqual(left, right, false);
            } else if (OGCJAXBStatics.FILTER_COMPARISON_ISLESS.equalsIgnoreCase(OpName)) {
                return filterFactory.less(left, right);
            } else if (OGCJAXBStatics.FILTER_COMPARISON_ISGREATER.equalsIgnoreCase(OpName)) {
                return filterFactory.greater(left, right);
            } else if (OGCJAXBStatics.FILTER_COMPARISON_ISLESSOREQUAL.equalsIgnoreCase(OpName)) {
                return filterFactory.lessOrEqual(left, right);
            } else if (OGCJAXBStatics.FILTER_COMPARISON_ISGREATEROREQUAL.equalsIgnoreCase(OpName)) {
                return filterFactory.greaterOrEqual(left, right);
            }

            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);

        } else if (ops instanceof org.geotoolkit.ogc.xml.v110.PropertyIsLikeType) {
            org.geotoolkit.ogc.xml.v110.PropertyIsLikeType property = (org.geotoolkit.ogc.xml.v110.PropertyIsLikeType) ops;

            Expression expr = filterFactory.property(property.getPropertyName().getContent());
            String pattern = visitExpression(property.getLiteralType()).toString();
            String wild = property.getWildCard();
            String single = property.getSingleChar();
            String escape = property.getEscapeChar();
            
            if (OGCJAXBStatics.FILTER_COMPARISON_ISLIKE.equalsIgnoreCase(OpName)) {
                return filterFactory.like(expr, pattern, wild, single, escape);
            }

            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);

        } else if (ops instanceof org.geotoolkit.ogc.xml.v110.PropertyIsBetweenType) {
            org.geotoolkit.ogc.xml.v110.PropertyIsBetweenType property = (org.geotoolkit.ogc.xml.v110.PropertyIsBetweenType) ops;

            Expression lower = visitExpression( property.getLowerBoundary().getExpression() );
            Expression upper = visitExpression( property.getUpperBoundary().getExpression() );
            Expression expr = visitExpression( property.getExpressionType() );
            
            if (OGCJAXBStatics.FILTER_COMPARISON_ISBETWEEN.equalsIgnoreCase(OpName)) {
                return filterFactory.between(expr, lower, upper);
            }
            
            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);

        } else if (ops instanceof org.geotoolkit.ogc.xml.v110.PropertyIsNullType) {
            org.geotoolkit.ogc.xml.v110.PropertyIsNullType property = (org.geotoolkit.ogc.xml.v110.PropertyIsNullType) ops;

            Expression expr = filterFactory.property(property.getPropertyName().getContent());
            
            if (OGCJAXBStatics.FILTER_COMPARISON_ISNULL.equalsIgnoreCase(OpName)) {
                return filterFactory.isNull(expr);
            }
            
            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);

        }
        
        throw new IllegalArgumentException("Unknowed filter element" + jax);
    }
    
    /**
     * Transform a SLD IDS Filter v1.1 in GT filter.
     */
    public Filter visitIds(List<JAXBElement<? extends org.geotoolkit.ogc.xml.v110.AbstractIdType>> lst){
        Set<Identifier> ids = new HashSet<Identifier>();
        
        for(JAXBElement<? extends org.geotoolkit.ogc.xml.v110.AbstractIdType> id : lst){
            AbstractIdType idd = id.getValue();
            if(idd instanceof FeatureIdType){
                ids.add( filterFactory.featureId(( (FeatureIdType)idd).getFid()) );
            }else if(idd instanceof GmlObjectIdType){
                ids.add( filterFactory.featureId(( (GmlObjectIdType)idd).getID()) );
            }
        }
        
        return filterFactory.id(ids);
    }
    
    public Expression visit(JAXBElement<? extends AbstractGeometryType> ele){
        throw new UnsupportedOperationException("not supported yet, need GML");
    }
    
    /**
     * Transform a JaxBelement in Expression.
     */
    public Expression visitExpression(JAXBElement<?> jax){
//        JAXBElementFunctionType>  ---NS
//        JAXBElementExpressionType> ---k
//        JAXBElementLiteralType> ---k
//        JAXBElementBinaryOperatorType> ---k
//        JAXBElementBinaryOperatorType> ---k
//        JAXBElementBinaryOperatorType> ---k
//        JAXBElementPropertyNameType>  ---k
//        JAXBElementBinaryOperatorType> ---k 
        
        String expName = jax.getName().getLocalPart();
        Object obj = jax.getValue();

        if(obj instanceof LiteralType){
            return visitExpression( (LiteralType)obj );
        }else if(obj instanceof BinaryOperatorType){
            BinaryOperatorType bot = (BinaryOperatorType) obj;
            Expression left = visitExpression(bot.getExpression().get(0));
            Expression right = visitExpression(bot.getExpression().get(1));
            
            if(OGCJAXBStatics.EXPRESSION_ADD.equalsIgnoreCase(expName)){
                return filterFactory.add(left, right);
            }else if(OGCJAXBStatics.EXPRESSION_DIV.equalsIgnoreCase(expName)){
                return filterFactory.divide(left, right);
            }else if(OGCJAXBStatics.EXPRESSION_MUL.equalsIgnoreCase(expName)){
                return filterFactory.multiply(left, right);
            }else if(OGCJAXBStatics.EXPRESSION_SUB.equalsIgnoreCase(expName)){
                return filterFactory.subtract(left, right);
            }
            
            throw new IllegalArgumentException("Unknowed expression element : Name > " + expName +"  JAXB > " + jax + " OBJECT >" + obj);
            
        }else if(obj instanceof PropertyNameType){
            PropertyNameType pnt = (PropertyNameType) obj;
            return filterFactory.property(pnt.getContent());
        }else if(obj instanceof FunctionType){
            FunctionType ft = (FunctionType) obj;
            Expression[] exps = new Expression[ft.getExpression().size()];
            
            int i=0;
            for(JAXBElement<?> ele : ft.getExpression()){
                exps[i] = visitExpression(ele);
                i++;
            }
            
            return filterFactory.function(ft.getName(), exps);
        }
        
        throw new IllegalArgumentException("Unknowed expression element" + jax);
    }
    
    /**
     * Transform a literalType in Expression.
     */
    public Expression visitExpression(LiteralType type){
        List<Object> content = type.getContent();
        
        for(Object obj : content){
            if(obj != null && !obj.toString().trim().isEmpty()){
                return filterFactory.literal(obj);
            }
        }
        return filterFactory.literal("");
    }
    
    
    /**
     * Change a QName in Name.
     */
    public Name visitQName(QName qname){
        if(qname == null) return null;
        return new DefaultName(qname);
    }
    
}
