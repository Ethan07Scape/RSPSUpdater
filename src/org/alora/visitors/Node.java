package org.alora.visitors;

import org.ethan.analyze.visitor.GraphVisitor;
import org.ethan.analyze.visitor.Searcher;
import org.ethan.analyze.visitor.VisitorInfo;
import org.ethan.hooks.FieldHookFrame;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

@VisitorInfo(hooks = {"uid", "previous", "next"})

public class Node extends GraphVisitor {
    private Searcher search;
    private AbstractInsnNode[] instructions;
    private FieldInsnNode fin;

    @Override
    public boolean validate(ClassNode cn) {
        return isNodeClass(cn);
    }

    @Override
    public void visit() {
        add("uid", getCn().getField(null, "J"));
        findNodeReferences();
    }

    public void findNodeReferences() {
        final List<MethodNode> methodList = getCn().methods;
        for (MethodNode m : methodList) {
            if (m.desc.equals("()V")) {
                instructions = m.instructions.toArray();
                search = new Searcher(m);
                int line = search.findSingleOpcode(ACONST_NULL, 0);
                line = search.find(new int[]{Opcodes.GETFIELD}, 0, line);
                if (line != -1) {
                    fin = ((FieldInsnNode) instructions[line]);
                    add(new FieldHookFrame("previous", fin));
                }
                line = search.find(new int[]{Opcodes.GETFIELD}, 2, line);
                if (line != -1) {
                    fin = ((FieldInsnNode) instructions[line]);
                    add(new FieldHookFrame("next", fin));
                }
            }
        }

    }

    private boolean isNodeClass(ClassNode cn) {
        if (cn.ownerless() && cn.fieldCount("J") >= 1) {
            if (cn.fieldCount("L" + cn.name + ";") == 2) {
                return true;
            }
        }
        return false;
    }
}
