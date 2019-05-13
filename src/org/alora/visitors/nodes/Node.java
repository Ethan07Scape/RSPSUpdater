package org.alora.visitors.nodes;

import org.ethan.analyze.visitor.GraphVisitor;
import org.ethan.analyze.visitor.Searcher;
import org.ethan.analyze.visitor.VisitorInfo;
import org.ethan.hooks.FieldHookFrame;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

@VisitorInfo(hooks = {"uid", "previous", "next"})

public class Node extends GraphVisitor {


    @Override
    public boolean validate(ClassNode cn) {
        return isNodeClass(cn);
    }

    @Override
    public void visit() {
        add("uid", getCn().getField(null, "J"));
        findNodes();
    }

    public void findNodes() {
        final List<MethodNode> methodList = getMethods(getCn(), methodNode -> methodNode.desc.equals("()V"));
        Searcher searcher;
        for (MethodNode m : methodList) {
            searcher = new Searcher(m);
            int line = searcher.findSingleOpcode(ACONST_NULL, 0);
            line = searcher.findPattern(new int[]{GETFIELD}, 0, line);
            if (line != -1) {
                add(new FieldHookFrame("previous", searcher.getField(line)));
            }
            line = searcher.findPattern(new int[]{GETFIELD}, 2, line);
            if (line != -1) {
                add(new FieldHookFrame("next", searcher.getField(line)));
            }
        }
    }

    private boolean isNodeClass(ClassNode cn) {
        if (cn.ownerless() && cn.fieldCount("J") >= 1) {
            return cn.fieldCount("L" + cn.name + ";") == 2;
        }
        return false;
    }
}
