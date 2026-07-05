package com.example.iobuild_kt.auth.presentation

import com.example.iobuild_kt.auth.domain.model.AuthenticatedUser
import com.example.iobuild_kt.auth.domain.repository.AuthRepository
import com.example.iobuild_kt.profile.domain.model.Profile
import com.example.iobuild_kt.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class FakeAuthRepository(
    var signUpResult: Result<Unit> = Result.success(Unit),
    var signInResult: Result<AuthenticatedUser> = Result.success(
        AuthenticatedUser(id = 42, email = "ana@example.com", role = "Builder", token = "tok")
    )
) : AuthRepository {
    var signUpCallCount = 0
    var signInCallCount = 0

    override suspend fun signIn(email: String, password: String): Result<AuthenticatedUser> {
        signInCallCount++
        return signInResult
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        signUpCallCount++
        return signUpResult
    }

    override suspend fun signOut() {}
    override suspend fun isLoggedIn(): Boolean = false
    override suspend fun getSavedUserId(): Int? = null
    override suspend fun getSavedUserRole(): String? = null
}

private class FakeProfileRepository(
    var createProfileResult: Result<Profile> = Result.success(
        Profile(1, 42, "", "Ana", "ana", "Dir", 30, "999", null)
    )
) : ProfileRepository {
    var createProfileCallCount = 0

    override suspend fun getProfile(userId: Int): Result<Profile> = createProfileResult
    override suspend fun updateProfile(profile: Profile): Result<Profile> =
        error("not used in this test")
    override suspend fun createProfile(
        userId: Int, photoUrl: String, name: String, username: String,
        address: String, age: Int, phoneNumber: String
    ): Result<Profile> {
        createProfileCallCount++
        return createProfileResult
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun fillValidAccountStep(vm: RegisterViewModel) {
        vm.onEmailChanged("ana@example.com")
        vm.onPasswordChanged("secret123")
        vm.onConfirmPasswordChanged("secret123")
        vm.submitAccountStep()
    }

    private fun fillValidProfileFields(vm: RegisterViewModel) {
        vm.onNameChanged("Ana Torres")
        vm.onUsernameChanged("ana")
        vm.onAddressChanged("Av. Siempre Viva 123")
        vm.onAgeChanged("30")
        vm.onPhoneNumberChanged("999999999")
    }

    @Test
    fun `submitAccountStep rejects mismatched passwords and stays on the account step`() {
        val vm = RegisterViewModel(FakeAuthRepository(), FakeProfileRepository())
        vm.onEmailChanged("ana@example.com")
        vm.onPasswordChanged("secret123")
        vm.onConfirmPasswordChanged("different")

        vm.submitAccountStep()

        assertEquals(RegisterStep.ACCOUNT, vm.state.value.step)
        assertTrue(vm.state.value.error != null)
    }

    @Test
    fun `submitAccountStep advances to the profile step when valid`() {
        val vm = RegisterViewModel(FakeAuthRepository(), FakeProfileRepository())

        fillValidAccountStep(vm)

        assertEquals(RegisterStep.PROFILE, vm.state.value.step)
    }

    @Test
    fun `submitProfileStep rejects age outside 18 to 100`() = runTest {
        val vm = RegisterViewModel(FakeAuthRepository(), FakeProfileRepository())
        fillValidAccountStep(vm)
        fillValidProfileFields(vm)
        vm.onAgeChanged("15")

        vm.submitProfileStep()

        assertTrue(vm.state.value.error != null)
        assertEquals(RegisterStep.PROFILE, vm.state.value.step)
    }

    @Test
    fun `submitProfileStep runs sign-up, sign-in and create-profile in order and reports success`() = runTest {
        val authRepo = FakeAuthRepository()
        val profileRepo = FakeProfileRepository()
        val vm = RegisterViewModel(authRepo, profileRepo)
        fillValidAccountStep(vm)
        fillValidProfileFields(vm)

        vm.submitProfileStep()

        assertEquals(1, authRepo.signUpCallCount)
        assertEquals(1, authRepo.signInCallCount)
        assertEquals(1, profileRepo.createProfileCallCount)
        assertTrue(vm.state.value.isSuccess)
    }

    @Test
    fun `retry after create-profile failure does not resend sign-up or sign-in`() = runTest {
        val authRepo = FakeAuthRepository()
        val profileRepo = FakeProfileRepository(
            createProfileResult = Result.failure(Throwable("server error"))
        )
        val vm = RegisterViewModel(authRepo, profileRepo)
        fillValidAccountStep(vm)
        fillValidProfileFields(vm)

        vm.submitProfileStep()
        assertEquals(1, authRepo.signUpCallCount)
        assertEquals(1, authRepo.signInCallCount)
        assertTrue(vm.state.value.error != null)

        profileRepo.createProfileResult = Result.success(
            Profile(1, 42, "", "Ana", "ana", "Dir", 30, "999", null)
        )
        vm.submitProfileStep()

        assertEquals(1, authRepo.signUpCallCount)
        assertEquals(1, authRepo.signInCallCount)
        assertEquals(2, profileRepo.createProfileCallCount)
        assertTrue(vm.state.value.isSuccess)
    }
}
