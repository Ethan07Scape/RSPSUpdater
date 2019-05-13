package org.alora.visitors.definitions;

import org.ethan.analyze.visitor.GraphVisitor;
import org.ethan.analyze.visitor.Searcher;
import org.ethan.analyze.visitor.VisitorInfo;
import org.ethan.hooks.FieldHookFrame;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;


@VisitorInfo(hooks = {"name", "id", "actions", "groundActions"})
public class ItemDefinition extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        if (cn.fieldCount("[Ljava/lang/String;") == 2) {
            return cn.getFieldTypeCount() == 8;
        }
        return false;
    }

    @Override
    public void visit() {
        add("name", getCn().getField(null, "Ljava/lang/String;"));
        findHooks();
    }

    public void findHooks() {
        MethodNode m = getFirstMethod(getCn(), methodNode -> methodNode.desc.equals("(L" + getCn().name + ";)L" + getCn().name + ";"));
        if (m != null) {
            Searcher search = new Searcher(m);
            FieldInsnNode id = search.getField(search.findSingleOpcode(GETFIELD, 0));
            if (id != null) {
                add(new FieldHookFrame("id", id));
            }
            FieldInsnNode actions = search.getField(search.findSingleOpcode(PUTFIELD, 3));
            if (actions != null) {
                add(new FieldHookFrame("actions", actions));
            }
            FieldNode groundActions = getFirstField(getCn(), fieldNode -> fieldNode.desc.equals("[Ljava/lang/String;") && !fieldNode.name.equals(actions.name));

            if (groundActions != null) {
                add(new FieldHookFrame("groundActions", groundActions));
            }
        }
    }

}