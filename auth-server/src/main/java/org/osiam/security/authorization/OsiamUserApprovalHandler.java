package org.osiam.security.authorization;

import org.osiam.resources.ClientSpring;
import org.osiam.security.authentication.ClientDetailsLoadingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.DefaultUserApprovalHandler;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;

/**
 * Osiam user approval handler extends the default user approval handler from spring.
 * It will add an implicit approval if configured in the client, so that the user is never asked to approve the client.
 * Additionally an expiry period can be configured if the implicit is not desired and the user need to approve once.
 * After that he will be asked again only if the period expires
 */
@Named("userApprovalHandler")
public class OsiamUserApprovalHandler extends DefaultUserApprovalHandler {

    private static final int MILLISECONDS = 1000;

    @Inject
    private ClientDetailsLoadingBean clientDetailsLoadingBean;


    /**
     * Is called if OsiamUserApprovalHandler.isApproved() returns false and AccessConfirmation is done by the user.
     * Than it will save the approve date to be able to check it as long as user accepts approval.
     * So the user is not bothered every time to approve the client.
     *
     * @param authorizationRequest spring authorizationRequest
     * @param userAuthentication   spring userAuthentication
     * @return the authorizationRequest
     */
    @Override
    public AuthorizationRequest updateBeforeApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
        //check if "user_oauth_approval" is in the authorizationRequests approvalParameters and the (size != 0)
        // -> true for accessConfirmation -> save actual date
        if (authorizationRequest.getApprovalParameters().size() != 0 && authorizationRequest.getApprovalParameters().containsKey("user_oauth_approval")
                && authorizationRequest.getApprovalParameters().get("user_oauth_approval").equals("true")) {
            ClientSpring client = getClientDetails(authorizationRequest);
            Date date = new Date(System.currentTimeMillis() + (client.getValidityInSeconds() * MILLISECONDS));
            client.setExpiry(date);

            clientDetailsLoadingBean.updateClient(client, authorizationRequest.getClientId());
        }
        return super.updateBeforeApproval(authorizationRequest, userAuthentication);
    }

    /**
     * Checks if the client is configured to not ask the user for approval or if the date to ask again expires.
     *
     * @param authorizationRequest spring authorizationRequest
     * @param userAuthentication   spring userAuthentication
     * @return whether user approved the client or not
     */
    @Override
    public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
        //check if implicit is configured in client or if user already confirmed approval once and validity time is not over
        ClientSpring client = getClientDetails(authorizationRequest);
        if (client.isImplicit()) {
            return true;
        } else if (client.getExpiry() != null && client.getExpiry().compareTo(new Date(System.currentTimeMillis())) >= 0) {
            return true;
        }
        return false;
    }

    private ClientSpring getClientDetails(AuthorizationRequest authorizationRequest) {
        return (ClientSpring) clientDetailsLoadingBean.loadClientByClientId(authorizationRequest.getClientId());
    }

}