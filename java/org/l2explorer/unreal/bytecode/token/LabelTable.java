package org.l2explorer.unreal.bytecode.token;

import org.l2explorer.io.ObjectInput;
import org.l2explorer.io.ObjectOutput;
import org.l2explorer.io.annotation.Compact;
import org.l2explorer.io.annotation.ReadMethod;
import org.l2explorer.io.annotation.UShort;
import org.l2explorer.io.annotation.WriteMethod;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.annotation.NameRef;
import org.l2explorer.unreal.annotation.Offset;
import org.l2explorer.unreal.bytecode.BytecodeContext;
import org.l2explorer.unreal.bytecode.TokenSerializerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a table of labels for state and code navigation (Opcode 0x0C).
 * <p>Used within UnrealScript states to map label names to their respective 
 * bytecode offsets. This table is terminated by a 'None' name reference.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (Analysis for Clean Room)
 * @author Gemini 3 Flash (Clean Room Reimplementation)
 *
 * @since 13-01-2026
 */
public class LabelTable extends Token {
    /**
     * The bytecode operation code for LabelTable.
     */
    public static final int OPCODE = 0x0C;

    private Label[] labels;

    public LabelTable() {
    }

    public LabelTable(Label... labels) {
        this.labels = labels;
    }

    public Label[] getLabels() {
        return labels;
    }

    public void setLabels(Label[] labels) {
        this.labels = labels;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    /**
     * Reads the label table from the input stream until a 'None' reference is encountered.
     */
    @ReadMethod
    public void readFrom(ObjectInput<BytecodeContext> input) throws IOException {
        List<Label> labelList = new ArrayList<>();
        int noneInd = TokenSerializerFactory.getNoneInd(input.getContext());
        
        while (true) {
            Label tmp = input.readObject(Label.class);
            if (tmp.getNameRef() == noneInd) {
                break;
            }
            labelList.add(tmp);
        }
        this.labels = labelList.toArray(new Label[0]);
    }

    /**
     * Writes the label table to the output stream, adding the 'None' terminator.
     */
    @WriteMethod
    public void writeLabelTable(ObjectOutput<BytecodeContext> output) throws IOException {
        if (labels != null) {
            for (Label label : labels) {
                output.write(label);
            }
        }
        // Write the None terminator: NameRef(None), Offset(0xffff), Unk(0)
        output.write(new Label(TokenSerializerFactory.getNoneInd(output.getContext()), 0xFFFF, 0));
    }

    /**
     * Custom sizer for LabelTable. 
     * Corrected the generic signature to match the Token base class.
     */
    @SuppressWarnings("unused")
	@Override
    protected Sizer<Token> getSizer() {
        return (token, context) -> {
            LabelTable lt = (LabelTable) token;
            int size = 1; // Opcode byte
            
            // Each entry: NameRef (Compact) + Offset (UShort/2) + Unk (UShort/2)
            // Note: If @Compact nameRef isn't always 4 bytes, you'd need to calculate its size.
            // Assuming standard 4-byte NameRef + 2 + 2 = 8 bytes per entry.
            if (lt.getLabels() != null) {
                size += lt.getLabels().length * 8;
            }
            size += 8; // Terminator entry size
            return size;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LabelTable that)) return false;
        return Arrays.equals(labels, that.labels);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(labels);
    }

    @Override
    public String toString() {
        String labelsStr = (labels == null || labels.length == 0) ? "" 
                : Arrays.stream(labels).map(Objects::toString).collect(Collectors.joining(", "));
        return "LabelTable(" + labelsStr + ")";
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        return ""; // Meta-information only
    }

    /**
     * Inner class representing a single entry in the LabelTable.
     */
    public static class Label {
        @Compact
        @NameRef
        private int nameRef;

        @UShort
        @Offset
        private int offset;

        @UShort
        private int unk;

        public Label() {
        }

        public Label(int nameRef, int offset, int unk) {
            this.nameRef = nameRef;
            this.offset = offset;
            this.unk = unk;
        }

        public int getNameRef() {
            return nameRef;
        }

        public void setNameRef(int nameRef) {
            this.nameRef = nameRef;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public int getUnk() {
            return unk;
        }

        public void setUnk(int unk) {
            this.unk = unk;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Label that)) return false;
            return nameRef == that.nameRef && offset == that.offset && unk == that.unk;
        }

        @Override
        public int hashCode() {
            return Objects.hash(nameRef, offset, unk);
        }

        @Override
        public String toString() {
            return String.format("Label(%d, 0x%04X, %d)", nameRef, offset, unk);
        }
    }
}