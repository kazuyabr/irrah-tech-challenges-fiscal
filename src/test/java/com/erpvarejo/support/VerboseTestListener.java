package com.erpvarejo.support;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestExecutionListener;

public class VerboseTestListener implements TestExecutionListener {

    private static final String PASS = "[PASSOU]";
    private static final String FAIL = "[FALHOU]";

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (testIdentifier.isTest()) {
            String displayName = testIdentifier.getDisplayName();
            System.out.println("  -> " + displayName);
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult result) {
        if (testIdentifier.isTest()) {
            String status = switch (result.getStatus()) {
                case SUCCESSFUL -> PASS;
                case FAILED -> FAIL;
                default -> "[IGNORADO]";
            };
            System.out.println("  <- " + status);
        }
    }
}
