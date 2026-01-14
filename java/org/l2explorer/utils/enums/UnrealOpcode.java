package org.l2explorer.utils.enums;

import java.util.HashMap;
import java.util.Map;

public enum UnrealOpcode {
    LOCAL_VARIABLE(0x00, "LocalVariable"),
    INSTANCE_VARIABLE(0x01, "InstanceVariable"),
    DEFAULT_VARIABLE(0x02, "DefaultVariable"),
    RETURN(0x04, "Return"),
    SWITCH(0x05, "Switch"),
    JUMP(0x06, "Jump"),
    JUMP_IF_NOT(0x07, "JumpIfNot"),
    STOP(0x08, "Stop"),
    ASSERT(0x09, "Assert"),
    CASE(0x0a, "Case"),
    NOTHING(0x0b, "Nothing"),
    LABEL_TABLE(0x0c, "LabelTable"),
    GOTO_LABEL(0x0d, "GotoLabel"),
    EAT_STRING(0x0e, "EatString"),
    LET(0x0f, "Let"),
    DYN_ARRAY_ELEMENT(0x10, "DynArrayElement"),
    NEW(0x11, "New"),
    CLASS_CONTEXT(0x12, "ClassContext"),
    METACAST(0x13, "Metacast"),
    LET_BOOL(0x14, "LetBool"),
    END_FUNCTION_PARAMS(0x16, "EndFunctionParams"),
    SELF(0x17, "Self"),
    SKIP(0x18, "Skip"),
    CONTEXT(0x19, "Context"),
    ARRAY_ELEMENT(0x1a, "ArrayElement"),
    VIRTUAL_FUNCTION(0x1b, "VirtualFunction"),
    FINAL_FUNCTION(0x1c, "FinalFunction"),
    INT_CONST(0x1d, "IntConst"),
    FLOAT_CONST(0x1e, "FloatConst"),
    STRING_CONST(0x1f, "StringConst"),
    OBJECT_CONST(0x20, "ObjectConst"),
    NAME_CONST(0x21, "NameConst"),
    ROTATOR_CONST(0x22, "RotatorConst"),
    VECTOR_CONST(0x23, "VectorConst"),
    BYTE_CONST(0x24, "ByteConst"),
    INT_ZERO(0x25, "IntZero"),
    INT_ONE(0x26, "IntOne"),
    TRUE(0x27, "True"),
    FALSE(0x28, "False"),
    NATIVE_PARAM(0x29, "NativeParam"),
    NO_OBJECT(0x2a, "NoObject"),
    INT_CONST_BYTE(0x2c, "IntConstByte"),
    BOOL_VARIABLE(0x2d, "BoolVariable"),
    DYNAMIC_CAST(0x2e, "DynamicCast"),
    ITERATOR(0x2f, "Iterator"),
    ITERATOR_POP(0x30, "IteratorPop"),
    ITERATOR_NEXT(0x31, "IteratorNext"),
    STRUCT_CMP_EQ(0x32, "StructCmpEq"),
    STRUCT_CMP_NE(0x33, "StructCmpNe"),
    STRUCT_MEMBER(0x36, "StructMember"),
    LENGTH(0x37, "Length"),
    GLOBAL_FUNCTION(0x38, "GlobalFunction"),
    CONVERSION_TABLE(0x39, "ConversionTable"),
    BYTE_TO_INT(0x3a, "ByteToInt"),
    BYTE_TO_BOOL(0x3b, "ByteToBool"),
    BYTE_TO_FLOAT(0x3c, "ByteToFloat"),
    INT_TO_BYTE(0x3d, "IntToByte"),
    INT_TO_BOOL(0x3e, "IntToBool"),
    INT_TO_FLOAT(0x3f, "IntToFloat"),
    BOOL_TO_BYTE(0x40, "BoolToByte"), // Note: Insert.java também usa 0x40
    BOOL_TO_INT(0x41, "BoolToInt"),   // Note: Remove.java também usa 0x41
    BOOL_TO_FLOAT(0x42, "BoolToFloat"),
    FLOAT_TO_BYTE(0x43, "FloatToByte"),
    FLOAT_TO_INT(0x44, "FloatToInt"), // Note: DelegateName.java também usa 0x44
    FLOAT_TO_BOOL(0x45, "FloatToBool"),
    STRING_TO_NAME(0x46, "StringToName"), // Note: INT64Const.java também usa 0x46
    OBJECT_TO_BOOL(0x47, "ObjectToBool"), // Note: DynArraySort.java também usa 0x47
    NAME_TO_BOOL(0x48, "NameToBool"),
    STRING_TO_BYTE(0x49, "StringToByte"),
    STRING_TO_INT(0x4a, "StringToInt"),
    STRING_TO_BOOL(0x4b, "StringToBool"),
    STRING_TO_FLOAT(0x4c, "StringToFloat"),
    STRING_TO_VECTOR(0x4d, "StringToVector"),
    STRING_TO_ROTATOR(0x4e, "StringToRotator"),
    VECTOR_TO_BOOL(0x4f, "VectorToBool"),
    VECTOR_TO_ROTATOR(0x50, "VectorToRotator"),
    ROTATOR_TO_BOOL(0x51, "RotatorToBool"),
    BYTE_TO_STRING(0x52, "ByteToString"),
    INT_TO_STRING(0x53, "IntToString"),
    BOOL_TO_STRING(0x54, "BoolToString"),
    FLOAT_TO_STRING(0x55, "FloatToString"),
    OBJECT_TO_STRING(0x56, "ObjectToString"),
    NAME_TO_STRING(0x57, "NameToString"),
    VECTOR_TO_STRING(0x58, "VectorToString"),
    ROTATOR_TO_STRING(0x59, "RotatorToString"),
    BYTE_TO_INT64(0x5a, "ByteToINT64"),
    INT_TO_INT64(0x5b, "IntToINT64"),
    BOOL_TO_INT64(0x5c, "BoolToINT64"),
    FLOAT_TO_INT64(0x5d, "FloatToINT64"),
    STRING_TO_INT64(0x5e, "StringToINT64"),
    INT64_TO_BYTE(0x5f, "INT64ToByte"),
    INT64_TO_INT(0x60, "INT64ToInt"),
    INT64_TO_BOOL(0x61, "INT64ToBool"),
    INT64_TO_FLOAT(0x62, "INT64ToFloat"),
    INT64_TO_STRING(0x63, "INT64ToString"),
    INT64_CONST(0x46, "INT64Const"), 
	CONCATEN(0x70, "Concaten"),
	EQUAL(0x97, "Equal"),
	DIFFERENCE(0x98, "Difference");

    private final int opcode;
    private final String name;

    UnrealOpcode(int opcode, String name) {
        this.opcode = opcode;
        this.name = name;
    }

    private static final Map<Integer, UnrealOpcode> LOOKUP = new HashMap<>();
    static {
        for (UnrealOpcode op : values()) {
            LOOKUP.put(op.opcode, op);
        }
    }

    public static UnrealOpcode fromInt(int op) {
        return LOOKUP.get(op);
    }

    public int getOpcode() { return opcode; }
    public String getName() { return name; }
}