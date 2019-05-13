package org.alora.visitors.nodes;

import org.ethan.analyze.visitor.GraphVisitor;
import org.ethan.analyze.visitor.VisitorInfo;
import org.ethan.hooks.FieldHookFrame;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.JumpNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

@VisitorInfo(hooks = {"tail", "head", "next", "current"})
public class NodeDeque extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        if (cn.ownerless() && cn.interfaces.isEmpty() && cn.fields.size() < 5 &&
                cn.fieldCount(desc("Node")) == 2 && cn.access == 48) {
            return true;
        }
        return false;
    }

    @Override
    public void visit() {
        visit(new NodeHooks());
        // methods();
    }

    private class NodeHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                @Override
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ACMPNE) {
                        FieldMemberNode fmn = jn.firstField();
                        String node = desc("Node");
                        if (fmn != null && fmn.desc().equals(node)) {
                            add(new FieldHookFrame("head", fmn.fin()));
                            for (FieldNode fn : getCn().fields) {
                                if (fn.desc.equals(node)) {
                                    if (!fn.name.equals(fmn.name())) {
                                        add(new FieldHookFrame("tail", fn));
                                        break;
                                    }
                                }
                            }
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    /*private void methods() {
        for (MethodNode mn : getCn().methods) {
            if (mn.desc.equals("()L" + clazz("Node") + ";") &&
                    mn.referenced(getUpdateEngine().getArchive().build().get("US"))) {
                int count = 0;
                int virt = 0;
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.opcode() == Opcodes.GETFIELD) {
                        count++;
                    } else if (ain.opcode() == Opcodes.INVOKEVIRTUAL) {
                        virt++;
                    }
                }
                if (count > 3 || virt == 1) {
                    getHooks().put("current", new InvokeHook("current", mn));
                } else if (count == 3) {
                    getHooks().put("next", new InvokeHook("next", mn));
                }
            }
        }
    }*/
}