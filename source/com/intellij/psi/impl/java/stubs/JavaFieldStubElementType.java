/*
 * @author max
 */
package com.intellij.psi.impl.java.stubs;

import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.impl.cache.InitializerTooLongException;
import com.intellij.psi.impl.cache.impl.repositoryCache.RecordUtil;
import com.intellij.psi.impl.cache.impl.repositoryCache.TypeInfo;
import com.intellij.psi.impl.java.stubs.impl.PsiFieldStubImpl;
import com.intellij.psi.impl.java.stubs.index.JavaFieldNameIndex;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.DataInputOutputUtil;
import com.intellij.util.io.PersistentStringEnumerator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class JavaFieldStubElementType extends JavaStubElementType<PsiFieldStub, PsiField> {
  public JavaFieldStubElementType() {
    super("java.FIELD");
  }

  public PsiField createPsi(final PsiFieldStub stub) {
    throw new UnsupportedOperationException("createPsi is not implemented"); // TODO
  }

  public PsiFieldStub createStub(final PsiField psi, final StubElement parentStub) {
    final PsiExpression initializer = psi.getInitializer();
    final TypeInfo type = TypeInfo.create(psi.getType(), psi.getTypeElement());
    return new PsiFieldStubImpl(parentStub, this, psi.getName(), type, initializer != null ? initializer.getText() : null, psi instanceof PsiEnumConstant);
  }

  public String getExternalId() {
    return "java.FIELD";
  }

  public void serialize(final PsiFieldStub stub, final DataOutputStream dataStream, final PersistentStringEnumerator nameStorage)
      throws IOException {
    DataInputOutputUtil.writeNAME(dataStream, stub.getName(), nameStorage);
    RecordUtil.writeTYPE(dataStream, stub.getType(), nameStorage);
    DataInputOutputUtil.writeNAME(dataStream, getInitializerText(stub), nameStorage);
    dataStream.writeBoolean(stub.isEnumConstant());
  }

  private static String getInitializerText(final PsiFieldStub stub) {
    try {
      return stub.getInitializerText();
    }
    catch (InitializerTooLongException e) {
      return PsiFieldStubImpl.INITIALIZER_TOO_LONG;
    }
  }

  public PsiFieldStub deserialize(final DataInputStream dataStream,
                                  final StubElement parentStub, final PersistentStringEnumerator nameStorage) throws IOException {
    String name = DataInputOutputUtil.readNAME(dataStream, nameStorage);

    final TypeInfo type = new TypeInfo();
    RecordUtil.readType(dataStream, type);

    String initializerText = DataInputOutputUtil.readNAME(dataStream, nameStorage);
    boolean isEnumConst = dataStream.readBoolean();

    return new PsiFieldStubImpl(parentStub, this, name, type, initializerText, isEnumConst);
  }

  public void indexStub(final PsiFieldStub stub, final IndexSink sink) {
    final String name = stub.getName();
    if (name != null) {
      sink.occurence(JavaFieldNameIndex.KEY, name);
    }
  }
}