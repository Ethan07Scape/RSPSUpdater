package org.alora.visitors.interactable;


import org.ethan.analyze.visitor.GraphVisitor;
import org.ethan.analyze.visitor.Searcher;
import org.ethan.analyze.visitor.VisitorInfo;
import org.ethan.hooks.FieldHookFrame;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.List;

@VisitorInfo(hooks = {"owner", "children", "itemId", "itemAmount",
        "id", "type", "itemIds", "stackSizes", "textureId", "index",
        "text", "ownerId", "boundsIndex", "actions", "hidden", "renderCycle"})

public class Widget extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        if (cn.fieldCount("[Ljava/lang/Object;") > 10) {
            return true;
        }
        return cn.superName.equals(clazz("Node")) && cn.fieldCount("[Ljava/lang/Object;") > 10;
    }

    @Override
    public void visit() {
        add("owner", getCn().getField(null, "L" + getCn().name + ";"));
        add("children", getCn().getField(null, "[L" + getCn().name + ";"));
        findItemInfo();
        findID();
        findActions();
        findRenderCycle();
    }

    private Searcher getNewSearcher(MethodNode m) {
        return new Searcher(m);
    }

    public void findActions() {
        List<FieldNode> f = getFields(getCn(), fieldNode -> fieldNode.desc.equals("[Ljava/lang/String;"));
        add(new FieldHookFrame("actions", f.get(1)));
    }

    public void findRenderCycle() {
        for (ClassNode cn : getUpdateEngine().getClassNodes().values()) {
            MethodNode m = getFirstMethod(cn, methodNode -> methodNode.instructions.size() > 5000 && getNewSearcher(methodNode).findIntValue(SIPUSH, 909) != -1
                    && getNewSearcher(methodNode).findIntValue(SIPUSH, 898) != -1);
            if (m != null) {
                Searcher search = new Searcher(m);
                int line = search.findIntValue(SIPUSH, 698);
                if (line != -1) {
                    FieldInsnNode f = search.getField(search.findPattern(new int[]{Opcodes.PUTFIELD, ALOAD, ILOAD, PUTFIELD}, 0, line));
                    add(new FieldHookFrame("renderCycle", f));
                }
            }
        }
    }

    public void findID() {
        ClassNode canvasClass = null;
        for (ClassNode cn : getUpdateEngine().getClassNodes().values()) {
            if (cn.name.equals(clazz("Canvas"))) {
                canvasClass = cn;
            }
        }
        MethodNode m = getFirstMethod(canvasClass, methodNode -> methodNode.instructions.size() > 100 && getNewSearcher(methodNode).findIntValue(BIPUSH, 16) != -1);
        if (m != null) {
            Searcher searcher = getNewSearcher(m);
            int line = searcher.findIntValue(BIPUSH, 16);
            if (line != -1) {
                FieldInsnNode f = searcher.getField(searcher.findPattern(new int[]{Opcodes.PUTFIELD}, 0, line));
                add(new FieldHookFrame("id", f));
            }
        }
    }
    public void findItemInfo() {
        for (ClassNode cn : getUpdateEngine().getClassNodes().values()) {
            MethodNode m = getFirstMethod(cn, methodNode -> methodNode.instructions.size() > 5000 && getNewSearcher(methodNode).findIntValue(SIPUSH, 1701) != -1);
            if (m != null) {
                Searcher search = new Searcher(m);
                int line = search.findIntValue(Opcodes.SIPUSH, 1701);
                if (line != -1) {
                    FieldInsnNode itemId = search.getField(search.findPattern(new int[]{Opcodes.GETFIELD}, 0, line));
                    if (itemId != null) {
                        add(new FieldHookFrame("itemId", itemId));
                    }
                }
                FieldInsnNode itemAmount = search.getField(search.findPattern(new int[]{Opcodes.GETFIELD}, 1, line));
                if (itemAmount != null) {
                    add(new FieldHookFrame("itemAmount", itemAmount));
                }

            }
        }
    }
}