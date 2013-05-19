package org.javaswift.joss.command.impl.factory;

import org.apache.http.client.HttpClient;
import org.javaswift.joss.command.impl.account.AccountInformationCommandImpl;
import org.javaswift.joss.command.impl.account.AccountMetadataCommandImpl;
import org.javaswift.joss.command.impl.account.ListContainersCommandImpl;
import org.javaswift.joss.command.impl.account.TenantCommandImpl;
import org.javaswift.joss.command.shared.account.AccountInformationCommand;
import org.javaswift.joss.command.shared.account.AccountMetadataCommand;
import org.javaswift.joss.command.shared.account.ListContainersCommand;
import org.javaswift.joss.command.shared.account.TenantCommand;
import org.javaswift.joss.command.shared.factory.AccountCommandFactory;
import org.javaswift.joss.command.shared.factory.ContainerCommandFactory;
import org.javaswift.joss.command.shared.identity.AuthenticationCommand;
import org.javaswift.joss.headers.Header;
import org.javaswift.joss.instructions.ListInstructions;
import org.javaswift.joss.model.Access;
import org.javaswift.joss.model.Account;

import java.util.Collection;

public class AccountCommandFactoryImpl implements AccountCommandFactory {

    private final ContainerCommandFactory containerCommandFactory;

    private final HttpClient httpClient;
    private Access access;
    private final AuthenticationCommand authCommand;
    private String publicHost;
    private String privateHost;

    public AccountCommandFactoryImpl(HttpClient httpClient, Access access, AuthenticationCommand authCommand) {
        this.httpClient = httpClient;
        this.access = access;
        this.authCommand = authCommand;
        this.containerCommandFactory = new ContainerCommandFactoryImpl(this);
    }

    @Override
    public void setPublicHost(String publicHost) {
        this.publicHost = publicHost;
    }

    @Override
    public void setPrivateHost(String privateHost) {
        this.privateHost = privateHost;
    }

    @Override
    public String getPublicHost() {
        return this.publicHost == null ? access.getPublicURL() : this.publicHost;
    }

    @Override
    public String getPrivateHost() {
        return this.privateHost == null ? access.getPublicURL() : this.privateHost;
    }

    @Override
    public String getOriginalHost() {
        return access.getPublicURL();
    }

    public Access authenticate() {
        return access = authCommand.call();
    }

    @Override
    public AccountInformationCommand createAccountInformationCommand(Account account) {
        return new AccountInformationCommandImpl(account, httpClient, access);
    }

    @Override
    public AccountMetadataCommand createAccountMetadataCommand(Account account, Collection<? extends Header> headers) {
        return new AccountMetadataCommandImpl(account, httpClient, access, headers);
    }

    @Override
    public ListContainersCommand createListContainersCommand(Account account, ListInstructions listInstructions) {
        return new ListContainersCommandImpl(account, httpClient, access, listInstructions);
    }

    @Override
    public TenantCommand createTenantCommand(Account account) {
        return new TenantCommandImpl(account, httpClient, access, authCommand.getUrl());
    }

    @Override
    public ContainerCommandFactory getContainerCommandFactory() {
        return this.containerCommandFactory;
    }

    @Override
    public boolean isTenantSupplied() {
        return this.access.isTenantSupplied();
    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public Access getAccess() {
        return this.access;
    }

}
