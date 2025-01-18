console.log("Email Reply Assistant - content script loaded");


// Replicate Gmail button
function createButton() {
    const button = document.createElement('div');
    button.className = 'T-I J-J5-Ji aoO v7 T-I-atl L3';
    button.style.marginRight = '8px';
    button.innerHTML = 'AI Reply';
    button.style.backgroundColor = '#2656C9'; 
    button.style.color = '#fff'; 
    button.style.borderRadius = '4px'; 
    button.style.boxShadow = 'none';
    button.style.padding = '0 8px'; 
    button.style.height = '36px'; 
    button.style.lineHeight = '36px'; 
    button.style.fontSize = '14px'; 
    button.style.fontFamily = "'Open Sans', Helvetica, Arial, 'Liberation Sans', sans-serif";
    button.style.cursor = 'pointer'; 
    button.style.display = 'inline-flex'; 
    button.style.alignItems = 'center'; 
    button.style.fontWeight = '525';
    button.style.justifyContent = 'center';
    button.style.outline = 'none';
    button.style.border = 'none';
    button.setAttribute('role', 'button');
    button.setAttribute('data-tooltip', 'Generate AI reply');
    return button;
}

/*
reply box class : Am aiL aO9 Al editable LW-avf tS-tW 
compose box class : Am aiL Al editable LW-avf tS-tW
*/

function isReplyBox() {
    const replyBox = document.querySelector('.aO9'); 
    return replyBox !== null; 
}

function getEmailMessageContent() {
    const selectors = [
        '.h7', 
        '.a3s aiL', 
        '.gmail_quote',
        '[role="presentation"]',
    ];

    for (const selector of selectors) {
        const content = document.querySelector(selector);
        if (content) {
            return content.innerText.trim();
        } 
    };

    return null;
}

function getEmailReplyToolBar() {
    // Possible selectors
    const selectors = [
        '.aDh', 
        '.btC', 
        '[role="toolbar"]',
        '.gU.Up',
    ];

    for (const selector of selectors) {
        const toolbar = document.querySelector(selector);
        if (toolbar) {
            return toolbar;
        } 
    };

    return null;
}

function injectButton() {
    if (!isReplyBox()) {
        console.log("Not a reply context. Skipping button injection.");
        return; 
    }
    const buttonClass = '.assistant-reply-button';
    const existingButton = document.querySelector(buttonClass);
    if (existingButton) {
        existingButton.remove();
    }

    const toolBar = getEmailReplyToolBar();
    if (!toolBar) {
        console.log("Error, the replies toolbar could not be located");
        return; 
    } 

    const sendButtonClass = '.T-I.J-J5-Ji.aoO';
    const sendButton = toolBar.querySelector(sendButtonClass);
    console.log("Send button located");
    if (!sendButton) {
        console.log("Send button not found in the toolbar");
    }

    console.log("Toolbar located, creating reply button...");
    const button = createButton();
    button.classList.add(buttonClass);
    button.addEventListener('click', async () => {
        try {
            const url = 'http://localHost:8080/emailApi/email/generateResponse';
            button.innerHTML= "Generating";
            button.disabled = true;
            const emailMessageContent = getEmailMessageContent();
            console.log("Email Message Content:", emailMessageContent);
            if (!emailMessageContent) {
                throw new Error("Unable to retrieve email content.");
            }
            //console.log("Payload being sent:", { emailMessageContent });

            const response = await fetch(url, {
                method: 'POST',
                headers : {
                    'Content-Type' : 'application/json',
                },
                body: JSON.stringify({
                    emailMessageContent,
                    emailReplyTone : "professional"
                })
            });

            if (!response.ok) {
                throw new Error("API request failed")
            }
            
            const reply = await response.text();
            const replyBox = document.querySelector(
                '[role="textbox"]'
            );
            //console.log("Reply Box Selector Test:", document.querySelector('[role="textbox"]'));
            if (replyBox) {
                replyBox.focus();
                replyBox.textContent = reply;
                const inputEvent = new InputEvent('input', {
                    bubbles: true,
                    cancelable: true,
                });
                replyBox.dispatchEvent(inputEvent);
            } else {
                throw new Error("Reply box not found.");
            }
        } catch (error) {
            console.error(error);
            alert(error.message);
        } finally {
            button.innerHTML = "AI reply";
            button.disabled = false; 
        }
    });

    //toolBar.insertBefore(button, toolBar.firstChild);
    sendButton.parentNode.insertBefore(button, sendButton);
}


// Observe for changes in Email (in message div)
const observer  = new MutationObserver((mutations) => {
    for (const mutation of mutations) {
        // Store changes
        const addedNodes = Array.from(mutation.addedNodes);
        // Check if node is element node (of gmail reply box - class names derived from gmail)
        const hasElementNodes = addedNodes.some(node => 
            node.nodeType === Node.ELEMENT_NODE && (node.matches('.aDh, .btC, [role="dialog"]') || 
            node.querySelector('.aDh, .btC, [role="dialog"]')
        ));
 
        if (hasElementNodes) {
            console.log("Message Reply box detected");
            setTimeout(injectButton, 500);
        }
    }
});

observer.observe(document.body, {
    childList: true,
    subtree: true
} );