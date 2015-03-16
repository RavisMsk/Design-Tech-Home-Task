/**
 * Created by Ravis on 27/02/15.
 */

function deleteUploadWithId(id) {
    $.ajax({
        url: '/uploads/'+id,
        type: 'DELETE',
        success: function(result) {
            location.reload();
        }
    });
}