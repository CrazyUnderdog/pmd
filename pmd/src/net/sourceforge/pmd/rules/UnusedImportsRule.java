/*
 * User: tom
 * Date: Aug 23, 2002
 * Time: 8:34:14 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.*;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.text.MessageFormat;

public class UnusedImportsRule extends AbstractRule {

    private Set imports = new HashSet();

    public Object visit(ASTCompilationUnit node, Object data) {
        imports.clear();

        super.visit(node, data);

        RuleContext ctx = (RuleContext)data;
        for (Iterator i = imports.iterator(); i.hasNext();) {
            ImportWrapper wrapper = (ImportWrapper)i.next();
            String msg = MessageFormat.format(getMessage(), new Object[] {wrapper.getName()});
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, wrapper.getLine(), msg));
        }
        return data;
    }

    public Object visit(ASTImportDeclaration node, Object data) {
        if (!node.isImportOnDemand()) {
            ASTName importedType = (ASTName)node.jjtGetChild(0);
            String className = null;
            if (importedType.getImage().indexOf('.') != -1) {
                int lastDot = importedType.getImage().lastIndexOf('.')+1;
                className = importedType.getImage().substring(lastDot);
            } else {
                className = importedType.getImage();
            }
            ImportWrapper wrapper = new ImportWrapper(className, node.getBeginLine());
            imports.add(wrapper);
        }

        return data;
    }

    public Object visit(ASTName node, Object data) {
        String name = null;
        if (node.getImage().indexOf('.') == -1) {
            name = node.getImage();
        } else {
            name = node.getImage().substring(0, node.getImage().indexOf('.'));
        }
        ImportWrapper candidate = new ImportWrapper(name, -1);
        if (imports.contains(candidate)) {
            imports.remove(candidate);
        }
        return data;
    }


}
