package org.l2explorer.utils.enums;

import java.util.HashMap;
import java.util.Map;
import org.l2explorer.unreal.bytecode.token.*;

public class UnrealOpcode {

    public enum Main {
        LOCAL_VARIABLE(0x00, LocalVariable.class, "LocalVariable"),
        INSTANCE_VARIABLE(0x01, InstanceVariable.class, "InstanceVariable"),
        DEFAULT_VARIABLE(0x02, DefaultVariable.class, "DefaultVariable"),
        STATE_VARIABLE(0x03, StateVariable.class, "StateVariable"),
        RETURN(0x04, Return.class, "Return"),
        SWITCH(0x05, Switch.class, "Switch"),
        JUMP(0x06, Jump.class, "Jump"),
        JUMP_IF_NOT(0x07, JumpIfNot.class, "JumpIfNot"),
        STOP(0x08, Stop.class, "Stop"),
        ASSERT(0x09, Assert.class, "Assert"),
        CASE(0x0a, Case.class, "Case"),
        NOTHING(0x0b, Nothing.class, "Nothing"),
        LABEL_TABLE(0x0c, LabelTable.class, "LabelTable"),
        GOTO_LABEL(0x0d, GotoLabel.class, "GotoLabel"),        
        EAT_STRING(0x0e, EatString.class, "EatString"),
        LET(0x0f, Let.class, "Let"),
        DYN_ARRAY_ELEMENT(0x10, DynArrayElement.class, "DynArrayElement"),
        NEW(0x11, New.class, "New"),
        CLASS_CONTEXT(0x12, ClassContext.class, "ClassContext"),
        METACAST(0x13, Metacast.class, "Metacast"),
        LET_BOOL(0x14, LetBool.class, "LetBool"),
        END_PARM_VALUES(0x15, EndParmValues.class, "EndParmValues"),
        END_FUNCTION_PARAMS(0x16, EndFunctionParams.class, "EndFunctionParams"),
        SELF(0x17, Self.class, "Self"),
        SKIP(0x18, Skip.class, "Skip"),
        CONTEXT(0x19, Context.class, "Context"),
        ARRAY_ELEMENT(0x1a, ArrayElement.class, "ArrayElement"),
        VIRTUAL_FUNCTION(0x1b, VirtualFunction.class, "VirtualFunction"),
        FINAL_FUNCTION(0x1c, FinalFunction.class, "FinalFunction"),
        INT_CONST(0x1d, IntConst.class, "IntConst"),
        FLOAT_CONST(0x1e, FloatConst.class, "FloatConst"),
        STRING_CONST(0x1f, StringConst.class, "StringConst"),
        OBJECT_CONST(0x20, ObjectConst.class, "ObjectConst"),
        NAME_CONST(0x21, NameConst.class, "NameConst"),
        ROTATOR_CONST(0x22, RotatorConst.class, "RotatorConst"),
        VECTOR_CONST(0x23, VectorConst.class, "VectorConst"),
        BYTE_CONST(0x24, ByteConst.class, "ByteConst"),
        INT_ZERO(0x25, IntZero.class, "IntZero"),
        INT_ONE(0x26, IntOne.class, "IntOne"),
        TRUE(0x27, True.class, "True"),
        FALSE(0x28, False.class, "False"),
        NATIVE_PARAM(0x29, NativeParam.class, "NativeParam"),
        NO_OBJECT(0x2a, NoObject.class, "NoObject"),
        DELEGATE_FUNCTION(0x2b, DelegateFunction.class, "DelegateFunction"),
        INT_CONST_BYTE(0x2c, IntConstByte.class, "IntConstByte"),
        BOOL_VARIABLE(0x2d, BoolVariable.class, "BoolVariable"),
        DYNAMIC_CAST(0x2e, DynamicCast.class, "DynamicCast"),
        ITERATOR(0x2f, Iterator.class, "Iterator"),
        ITERATOR_POP(0x30, IteratorPop.class, "IteratorPop"),
        ITERATOR_NEXT(0x31, IteratorNext.class, "IteratorNext"),
        STRUCT_CMP_EQ(0x32, StructCmpEq.class, "StructCmpEq"),
        STRUCT_CMP_NE(0x33, StructCmpNe.class, "StructCmpNe"),
        STRUCT_CONST(0x34, StructConst.class, "StructConst"),
        RANGE_CONST(0x35, RangeConst.class, "RangeConst"),
        STRUCT_MEMBER(0x36, StructMember.class, "StructMember"),
        LENGTH(0x37, Length.class, "Length"),
        GLOBAL_FUNCTION(0x38, GlobalFunction.class, "GlobalFunction"),
        CONVERSION_TABLE(0x39, ConversionTable.class, "ConversionTable"),
        INSERT(0x40, Insert.class, "Insert"),
        REMOVE(0x41, Remove.class, "Remove"),
        DELEGATE_NAME(0x44, DelegateName.class, "DelegateName"),
        INT64_CONST(0x46, INT64Const.class, "INT64Const"),
        DYN_ARRAY_SORT(0x47, DynArraySort.class, "DynArraySort"),
        BYTE_TO_INT(0x3a, ByteToInt.class, "ByteToInt"),
        BYTE_TO_BOOL(0x3b, ByteToBool.class, "ByteToBool"),
        BYTE_TO_FLOAT(0x3c, ByteToFloat.class, "ByteToFloat"),
        INT_TO_BYTE(0x3d, IntToByte.class, "IntToByte"),
        INT_TO_BOOL(0x3e, IntToBool.class, "IntToBool"),
        INT_TO_FLOAT(0x3f, IntToFloat.class, "IntToFloat"),
        BOOL_TO_BYTE(0x40, BoolToByte.class, "BoolToByte"),
        BOOL_TO_INT(0x41, BoolToInt.class, "BoolToInt"),
        BOOL_TO_FLOAT(0x42, BoolToFloat.class, "BoolToFloat"),
        FLOAT_TO_BYTE(0x43, FloatToByte.class, "FloatToByte"),
        FLOAT_TO_INT(0x44, FloatToInt.class, "FloatToInt"),
        FLOAT_TO_BOOL(0x45, FloatToBool.class, "FloatToBool"),
        STRING_TO_NAME(0x46, StringToName.class, "StringToName"),
        OBJECT_TO_BOOL(0x47, ObjectToBool.class, "ObjectToBool"),
        NAME_TO_BOOL(0x48, NameToBool.class, "NameToBool"),
        STRING_TO_BYTE(0x49, StringToByte.class, "StringToByte"),
        STRING_TO_INT(0x4a, StringToInt.class, "StringToInt"),
        STRING_TO_BOOL(0x4b, StringToBool.class, "StringToBool"),
        STRING_TO_FLOAT(0x4c, StringToFloat.class, "StringToFloat"),
        STRING_TO_VECTOR(0x4d, StringToVector.class, "StringToVector"),
        STRING_TO_ROTATOR(0x4e, StringToRotator.class, "StringToRotator"),
        VECTOR_TO_BOOL(0x4f, VectorToBool.class, "VectorToBool"),
        VECTOR_TO_ROTATOR(0x50, VectorToRotator.class, "VectorToRotator"),
        ROTATOR_TO_BOOL(0x51, RotatorToBool.class, "RotatorToBool"),
        BYTE_TO_STRING(0x52, ByteToString.class, "ByteToString"),
        INT_TO_STRING(0x53, IntToString.class, "IntToString"),
        BOOL_TO_STRING(0x54, BoolToString.class, "BoolToString"),
        FLOAT_TO_STRING(0x55, FloatToString.class, "FloatToString"),
        OBJECT_TO_STRING(0x56, ObjectToString.class, "ObjectToString"),
        NAME_TO_STRING(0x57, NameToString.class, "NameToString"),
        VECTOR_TO_STRING(0x58, VectorToString.class, "VectorToString"),
        ROTATOR_TO_STRING(0x59, RotatorToString.class, "RotatorToString"),
        BYTE_TO_INT64(0x5a, ByteToINT64.class, "ByteToINT64"),
        MBYTE_TO_INT64(0x5a, MByteToINT64.class, "MByteToINT64"),
        INT_TO_INT64(0x5b, IntToINT64.class, "IntToINT64"),
        BOOL_TO_INT64(0x5c, BoolToINT64.class, "BoolToINT64"),
        FLOAT_TO_INT64(0x5d, FloatToINT64.class, "FloatToINT64"),
        STRING_TO_INT64(0x5e, StringToINT64.class, "StringToINT64"),
        INT64_TO_BYTE(0x5f, INT64ToByte.class, "INT64ToByte"),
        INT64_TO_INT(0x60, INT64ToInt.class, "INT64ToInt"),
        INT64_TO_BOOL(0x61, INT64ToBool.class, "INT64ToBool"),
        INT64_TO_FLOAT(0x62, INT64ToFloat.class, "INT64ToFloat"),
        INT64_TO_STRING(0x63, INT64ToString.class, "INT64ToString");
    	
        private final int opcode;
        private final Class<? extends Token> bytecode;
        private final String name;

        Main(int opcode, Class<? extends Token> mainclass, String name) {
            this.opcode = opcode;
            this.bytecode = mainclass;
            this.name = name;
            }

        public int getOpcode() { return opcode; }
        public Class<? extends Token> getBytecode() { return bytecode; }
        public String getName() { return name; }

        private static final Map<Integer, Main> LOOKUP = new HashMap<>();
        static {
            for (Main op : values()) LOOKUP.put(op.opcode, op);
        }
        public static Main fromInt(int op) { return LOOKUP.get(op); }
    }
}