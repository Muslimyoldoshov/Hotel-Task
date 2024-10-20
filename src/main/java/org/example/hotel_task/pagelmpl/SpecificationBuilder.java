package org.example.hotel_task.pagelmpl;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import org.example.hotel_task.pagelmpl.CustomRsqlVisitor;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationBuilder
{
    public static <ENTITY> Specification<ENTITY> build( String predicate )
    {
        if( predicate == null || predicate.isBlank() )
        {
            return null;
        }
        Node rootNode = new RSQLParser( RSQLOperators.defaultOperators() ).parse( predicate );
        return rootNode.accept( new CustomRsqlVisitor<>() );
    }
}