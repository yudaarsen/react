export function validateEmail(email) {
    return String(email)
      .toLowerCase()
      .match(
        /^(.+)@(\S+)$/
      );
};

export function renewToken(navigate, dest) {
  let refreshToken = localStorage.getItem('refreshToken');
  if(refreshToken == null || refreshToken.length == 0) {
    navigate('/login', { state: {destination: dest }});
    return;
  }

  fetch(process.env.REACT_APP_API_HOST + '/refresh?refreshToken=' + refreshToken, {
    method: 'POST',
    credentials: "include"
  })
  .then((response) => {
    if(response.ok) {
      return response.json();
    } else {
      navigate('/login', { state: {destination: dest }});
      throw new Error('Incorrect token');
    }
  })
  .then((data) => {
    localStorage.setItem('refreshToken', data['refresh_token']);
    window.location.reload();
  })
  .catch((error) => null);
}

export function formatDateTime(datetime) {
  return new Date(datetime).toLocaleString('ru-RU');
}