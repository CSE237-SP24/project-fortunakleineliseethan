package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import bankapp.LLMInterface;

public class LLMInterfaceTests {
    
    @Test
    public void testSendQueryToLLM_InvalidEndpointURL() {
        String userInput = "Can you please let us know more details about your ";
        String context = "";
        assertThrows(IOException.class, () -> LLMInterface.sendQueryToLLM(userInput, context));
    }

    @Test
    public void testSendQueryToLLM_WrongAcceptHeader() throws IOException {
        String userInput = "Can you please let us know more details about your ";
        String context = "";

        assertThrows(IOException.class, () -> LLMInterface.sendQueryToLLM(userInput, context));
    }


}